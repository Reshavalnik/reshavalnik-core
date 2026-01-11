import base64
import json
import random
from os import mkdir
from os.path import isdir
# import text_to_image
# from sympy import preview #,symbols,Symbol,srepr,init_printing
## from IPython.display import Markdown
# from sympy.parsing.latex import parse_latex
from PIL import Image, ImageDraw, ImageFont
import os
import textwrap


def load_font(size):
    try:
        return ImageFont.truetype('arial.ttf', size)
    except Exception:
        try:
            return ImageFont.truetype('DejaVuSans.ttf', size)
        except Exception:
            return ImageFont.load_default()


def to_data_uri(path_value):
    if not path_value:
        return ""
    try:
        with open(path_value, "rb") as image_file:
            encoded = base64.b64encode(image_file.read()).decode("ascii")
        return "data:image/png;base64," + encoded
    except Exception:
        return ""


def point_on_img(filename='01.png'):
	# prepare Width
	w_image = 800
	h_image = w_image
	# create image
	image1 = Image.new(mode="RGB", size=(w_image, h_image), color="black")
	#	image2 = Image.new(mode="RGB", size=(w_image, h_image), color="black")
	draw = ImageDraw.Draw(image1)
	
	draw.ellipse((100, 370, 110, 380), fill=(255, 0, 0), outline=(0, 0, 0), width=1)
	draw.ellipse((500, 370, 510, 380), fill=(255, 0, 0), outline=(0, 0, 0), width=1)
	draw.ellipse((150, 220, 160, 230), fill=(255, 0, 0), outline=(0, 0, 0), width=1)
	draw.ellipse((300, 220, 310, 230), fill=(255, 0, 0), outline=(0, 0, 0), width=1)
	
	draw.line((105, 375, 505, 375), fill=(255, 255, 0), width=1)
	draw.line((105, 375, 155, 225), fill=(255, 255, 0), width=1)
	draw.line((155, 225, 305, 225), fill=(255, 255, 0), width=1)
	draw.line((305, 225, 505, 375), fill=(255, 255, 0), width=1)
	
	draw.line((155, 225, 245, 375), fill=(255, 255, 0), width=1)
	draw.line((305, 225, 245, 375), fill=(255, 255, 0), width=1)
	
	I1 = ImageDraw.Draw(image1)
	size = 20
	font = load_font(size)
	
	draw.text((110, 380), 'A', font=font, fill=(255, 255, 255))
	draw.text((510, 380), 'B', font=font, fill=(255, 255, 255))
	draw.text((310, 230), 'C', font=font, fill=(255, 255, 255))
	draw.text((160, 230), 'D', font=font, fill=(255, 255, 255))
	
	draw.text((260, 380), 'T', font=font, fill=(255, 255, 255))
	
	
	# save file
	image1.save(filename)
	return filename


import random
answers = ["А", "А", "А", "А", "А", "А", "А", "А", "А", "А"]
k = 7
N = random.randint(1, 4)
base_dir = os.getcwd()
os.makedirs(base_dir, exist_ok=True)
taskName = 'T_8_5_18'
imagesIndex = str(N) + '.png'
task_dir = os.path.join(base_dir, taskName)
os.makedirs(task_dir, exist_ok=True)
aa = "  Даден е трапец ABCD. Ъглополовящите на ъглите С и D на трапеца се пресичат в точка Т. \n"
aa2 = " Ако Т принадлежи на АВ, да се докаже, че AB = AD + BC. "
aa = aa + aa2
image_path = point_on_img(os.path.join(task_dir, 'imq1' + imagesIndex))
aaa = " Използвайте първата част от решението на задача T_8_5_4.  "
bbb = " Нека точка Т лежи върху АВ. Ъгъл ATD = ъгъл СDТ като кръстни ъгли. Ъгъл ADТ = ъгъл СDТ по условие. \n"
ddd = " Следователно  ъгъл ADТ = ъгъл АТD, откъдето следва AD = AT. Аналогично се доказва, че ВС = ВТ. \n"
ddd1 = " От двете равенства следва AB = АТ + ВТ = AD + BC. "
bbb = bbb + ddd + ddd1

solution_uri = to_data_uri(image_path)

options = {"А": "", "Б": "", "В": "", "Г": ""}

payload = {
    "task": aa.strip(),
    "options": options,
    "answer": answers[k],
    "hint": aaa,
    "solution": bbb,
    "images": {"task": [], "solution": [solution_uri] if solution_uri else []},
}
print(json.dumps(payload, ensure_ascii=False))
