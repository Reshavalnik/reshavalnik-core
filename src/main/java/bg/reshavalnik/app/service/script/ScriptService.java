package bg.reshavalnik.app.service.script;

import static bg.reshavalnik.app.exceptions.message.ErrorMessage.*;

import com.mongodb.client.gridfs.model.GridFSFile;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class ScriptService {

    private static final Logger log = LoggerFactory.getLogger(ScriptService.class);

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
    public String generate(String id) throws IOException, InterruptedException {
        log.info("Generate file with ID: {}", id);
        // Retrieving from GridFS
        GridFSFile gridFile = gridFs.findOne(Query.query(Criteria.where("_id").is(id)));
        if (gridFile == null) {
            throw new FileNotFoundException(FILE_NOT_FOUND + id);
        }
        GridFsResource resource = gridFs.getResource(gridFile);

        // We record temporarily to disk
        Path dir = Path.of(tempFilePath);
        Path tempPath = Files.createTempFile(dir, "script-", ".py");
        File temp = tempPath.toFile();
        try (InputStream in = resource.getInputStream();
                OutputStream out = new FileOutputStream(temp)) {
            StreamUtils.copy(in, out);
        } catch (IOException e) {
            throw new IOException(ERROR_WRITING_TEMPORARY_FILE + e.getMessage(), e);
        }

        // We start python
        ProcessBuilder pb = new ProcessBuilder("python", temp.getAbsolutePath());
        pb.environment().put("PYTHONIOENCODING", "UTF-8");
        pb.redirectErrorStream(true);
        Process p = pb.start();

        // read output
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

        // wait for process to finish
        int code = 0;
        try {
            code = p.waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new InterruptedException(PROCESS_WAS_INTERRUPTED + e.getMessage());
        }

        // remove temp file
        temp.delete();

        return String.format("Exit code: %d%n%s", code, output);
    }
}
