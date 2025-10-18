package bg.reshavalnik.app.service.script;

import static bg.reshavalnik.app.exceptions.message.ErrorMessage.*;

import com.mongodb.client.gridfs.model.GridFSFile;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Service
@Slf4j
public class ScriptService {

    @Value("${python.bin:python}")
    private String pythonBin;

    private final GridFsTemplate gridFs;

    private final String tempFilePath;

    public ScriptService(
            GridFsTemplate gridFs,
            @Value("${temp.file.path:${java.io.tmpdir}}") String tempFilePath) {
        this.gridFs = gridFs;
        this.tempFilePath = tempFilePath;
    }

    /** Writes a Python file to GridFS and returns the generated ID. */
    public String createTask(MultipartFile file) throws IOException {
        log.info("Creating file with name: {}", file.getOriginalFilename());
        return gridFs.store(
                        file.getInputStream(), file.getOriginalFilename(), file.getContentType())
                .toString();
    }

    /** Writes and remove old Python file to GridFS and returns the generated ID. */
    public String update(MultipartFile file, String fileId) throws IOException {
        gridFs.delete(new Query(Criteria.where("_id").is(fileId)));
        return createTask(file);
    }

    /** Runs a script by ID and returns stdout+stderr. */

    /** 1) Downloads a file from GridFS ONCE and returns the Path to the temporary .py */
    private Path prepareScript(String id) throws IOException {
        GridFSFile gridFile = gridFs.findOne(Query.query(Criteria.where("_id").is(id)));
        if (gridFile == null) {
            throw new FileNotFoundException(FILE_NOT_FOUND + id);
        }
        GridFsResource resource = gridFs.getResource(gridFile);

        Path dir = Path.of(tempFilePath);
        Files.createDirectories(dir);

        Path tempPath = Files.createTempFile(dir, "script-", ".py");
        try (InputStream in = resource.getInputStream();
                OutputStream out = Files.newOutputStream(tempPath)) {
            StreamUtils.copy(in, out);
        } catch (IOException e) {
            // if the save fails, delete the temp file
            try {
                Files.deleteIfExists(tempPath);
            } catch (Exception ignore) {
            }
            throw new IOException(ERROR_WRITING_TEMPORARY_FILE + e.getMessage(), e);
        }
        return tempPath;
    }

    /** 2) Runs a prepared Python script ONE time and returns stdout+stderr as a String */
    private String runOnce(Path scriptPath) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(pythonBin, scriptPath.toAbsolutePath().toString());
        pb.environment().put("PYTHONIOENCODING", "UTF-8");
        pb.redirectErrorStream(true);

        Process p = pb.start();

        StringBuilder output = new StringBuilder();
        try (BufferedReader reader =
                new BufferedReader(
                        new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            throw new IOException(ERROR_READING_PROCESS_OUTPUT + e.getMessage(), e);
        }

        int code;
        try {
            code = p.waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new InterruptedException(PROCESS_WAS_INTERRUPTED + e.getMessage());
        }

        return String.format("Exit code: %d%n%s", code, output);
    }

    /**
     * 3) Public method: execute the script by ID N times (without reading GridFS multiple times).
     */
    public List<String> generate(String id, int count) throws IOException, InterruptedException {
        if (count <= 0) return java.util.Collections.emptyList();

        Path script = prepareScript(id);
        try {
            List<String> results = new java.util.ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                results.add(runOnce(script));
            }
            return results;
        } finally {
            try {
                Files.deleteIfExists(script);
            } catch (Exception ignore) {
            }
        }
    }
}
