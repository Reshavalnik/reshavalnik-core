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


def encode_png_data_uri(path_value):
    with open(path_value, "rb") as image_file:
        encoded = base64.b64encode(image_file.read()).decode("ascii")
    return "data:image/png;base64," + encoded



def point_on_img(filename='01.png'):
    # prepare Width
    w_image = 800
    h_image = w_image

    FOREGROUND = (0, 0, 255)
    # create image
    image1 = Image.new(mode="RGB", size=(w_image, h_image), color="black")

    draw = ImageDraw.Draw(image1)


    draw.line((200, 200, 650, 200), fill=(255, 0, 0), width=1)
    draw.line((200, 250, 650, 250), fill=(255, 0, 0), width=1)
    draw.line((200, 300, 650, 300), fill=(0, 0, 255), width=1)
    draw.line((200, 350, 650, 350), fill=(0, 0, 255), width=1)
    draw.line((200, 400, 650, 400), fill=(0, 0, 255), width=1)
    draw.line((200, 450, 650, 450), fill=(0, 0, 255), width=1)
    draw.line((200, 500, 650, 500), fill=(0, 0, 255), width=1)
    draw.line((200, 550, 650, 550), fill=(0, 0, 255), width=1)
    draw.line((200, 600, 650, 600), fill=(0, 0, 255), width=1)
    draw.line((200, 650, 650, 650), fill=(255, 0, 0), width=1)
    #
    draw.line((200, 200, 200, 650), fill=(255, 0, 0), width=1)
    draw.line((250, 200, 250, 650), fill=(255, 0, 0), width=1)
    draw.line((300, 200, 300, 650), fill=(0, 0, 255), width=1)
    draw.line((350, 200, 350, 650), fill=(0, 0, 255), width=1)
    draw.line((400, 200, 400, 650), fill=(0, 0, 255), width=1)
    draw.line((450, 200, 450, 650), fill=(0, 0, 255), width=1)
    draw.line((500, 200, 500, 650), fill=(0, 0, 255), width=1)
    draw.line((550, 200, 550, 650), fill=(0, 0, 255), width=1)
    draw.line((600, 200, 600, 650), fill=(0, 0, 255), width=1)
    draw.line((650, 200, 650, 650), fill=(255, 0, 0), width=1)


    I1 = ImageDraw.Draw(image1)
    size = 20
    font = load_font(size)
    draw.text((215, 215), '1', font=font, fill=(255, 255, 255))
    draw.text((265, 215), '2', font=font, fill=(255, 255, 255))
    draw.text((315, 215), '3', font=font, fill=(255, 255, 255))
    draw.text((365, 215), '4', font=font, fill=(255, 255, 255))
    draw.text((415, 215), '5', font=font, fill=(255, 255, 255))
    draw.text((465, 215), '6', font=font, fill=(255, 255, 255))
    draw.text((515, 215), '7', font=font, fill=(255, 255, 255))
    draw.text((565, 215), '8', font=font, fill=(255, 255, 255))
    draw.text((615, 215), '9', font=font, fill=(255, 255, 255))

    draw.text((215, 265), '2', font=font, fill=(255, 255, 255))
    draw.text((265, 265), '4', font=font, fill=(255, 255, 255))
    draw.text((315, 265), '6', font=font, fill=(255, 255, 255))
    draw.text((365, 265), '8', font=font, fill=(255, 255, 255))
    draw.text((410, 265), '10', font=font, fill=(255, 255, 255))
    draw.text((460, 265), '12', font=font, fill=(255, 255, 255))
    draw.text((510, 265), '14', font=font, fill=(255, 255, 255))
    draw.text((560, 265), '16', font=font, fill=(255, 255, 255))
    draw.text((610, 265), '18', font=font, fill=(255, 255, 255))

    draw.text((215, 315), '3', font=font, fill=(255, 255, 255))
    draw.text((265, 315), '6', font=font, fill=(255, 255, 255))
    draw.text((315, 315), '9', font=font, fill=(255, 255, 255))
    draw.text((360, 315), '12', font=font, fill=(255, 255, 255))
    draw.text((410, 315), '15', font=font, fill=(255, 255, 255))
    draw.text((460, 315), '18', font=font, fill=(255, 255, 255))
    draw.text((510, 315), '21', font=font, fill=(255, 255, 255))
    draw.text((560, 315), '24', font=font, fill=(255, 255, 255))
    draw.text((610, 315), '27', font=font, fill=(255, 255, 255))

    draw.text((215, 365), '4', font=font, fill=(255, 255, 255))
    draw.text((265, 365), '8', font=font, fill=(255, 255, 255))
    draw.text((310, 365), '12', font=font, fill=(255, 255, 255))
    draw.text((360, 365), '16', font=font, fill=(255, 255, 255))
    draw.text((410, 365), '20', font=font, fill=(255, 255, 255))
    draw.text((460, 365), '24', font=font, fill=(255, 255, 255))
    draw.text((510, 365), '28', font=font, fill=(255, 255, 255))
    draw.text((560, 365), '32', font=font, fill=(255, 255, 255))
    draw.text((610, 365), '36', font=font, fill=(255, 255, 255))

    draw.text((215, 415), '5', font=font, fill=(255, 255, 255))
    draw.text((260, 415), '10', font=font, fill=(255, 255, 255))
    draw.text((310, 415), '15', font=font, fill=(255, 255, 255))
    draw.text((360, 415), '20', font=font, fill=(255, 255, 255))
    draw.text((410, 415), '25', font=font, fill=(255, 255, 255))
    draw.text((460, 415), '30', font=font, fill=(255, 255, 255))
    draw.text((510, 415), '35', font=font, fill=(255, 255, 255))
    draw.text((560, 415), '40', font=font, fill=(255, 255, 255))
    draw.text((610, 415), '45', font=font, fill=(255, 255, 255))

    draw.text((215, 465), '6', font=font, fill=(255, 255, 255))
    draw.text((260, 465), '12', font=font, fill=(255, 255, 255))
    draw.text((310, 465), '18', font=font, fill=(255, 255, 255))
    draw.text((360, 465), '24', font=font, fill=(255, 255, 255))
    draw.text((410, 465), '30', font=font, fill=(255, 255, 255))
    draw.text((460, 465), '36', font=font, fill=(255, 255, 255))
    draw.text((510, 465), '42', font=font, fill=(255, 255, 255))
    draw.text((560, 465), '48', font=font, fill=(255, 255, 255))
    draw.text((610, 465), '54', font=font, fill=(255, 255, 255))

    draw.text((215, 515), '7', font=font, fill=(255, 255, 255))
    draw.text((260, 515), '14', font=font, fill=(255, 255, 255))
    draw.text((310, 515), '21', font=font, fill=(255, 255, 255))
    draw.text((360, 515), '28', font=font, fill=(255, 255, 255))
    draw.text((410, 515), '35', font=font, fill=(255, 255, 255))
    draw.text((460, 515), '42', font=font, fill=(255, 255, 255))
    draw.text((510, 515), '49', font=font, fill=(255, 255, 255))
    draw.text((560, 515), '56', font=font, fill=(255, 255, 255))
    draw.text((610, 515), '63', font=font, fill=(255, 255, 255))

    draw.text((215, 565), '8', font=font, fill=(255, 255, 255))
    draw.text((260, 565), '16', font=font, fill=(255, 255, 255))
    draw.text((310, 565), '24', font=font, fill=(255, 255, 255))
    draw.text((360, 565), '32', font=font, fill=(255, 255, 255))
    draw.text((410, 565), '40', font=font, fill=(255, 255, 255))
    draw.text((460, 565), '48', font=font, fill=(255, 255, 255))
    draw.text((510, 565), '56', font=font, fill=(255, 255, 255))
    draw.text((560, 565), '64', font=font, fill=(255, 255, 255))
    draw.text((610, 565), '72', font=font, fill=(255, 255, 255))

    draw.text((215, 615), '9', font=font, fill=(255, 255, 255))
    draw.text((260, 615), '18', font=font, fill=(255, 255, 255))
    draw.text((310, 615), '27', font=font, fill=(255, 255, 255))
    draw.text((360, 615), '36', font=font, fill=(255, 255, 255))
    draw.text((410, 615), '45', font=font, fill=(255, 255, 255))
    draw.text((460, 615), '54', font=font, fill=(255, 255, 255))
    draw.text((510, 615), '63', font=font, fill=(255, 255, 255))
    draw.text((560, 615), '72', font=font, fill=(255, 255, 255))
    draw.text((610, 615), '81', font=font, fill=(255, 255, 255))

    # save file
    image1.save(filename)
    # show file


prime = [2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97]



import random
from fractions import Fraction
from decimal import Decimal
answers = ["А", "А", "А", "А", "А", "А", "А", "А", "А", "А"]
k = 0
A1 = random.randint(2, 9)
A2 = random.randint(2, 9)
A11 = " Съдържат числата от 1 до 9, записани последователно. "
A12 = " Съдържат числата от 2 до 10, записани последователно. "
A13 = " Съдържат числата от 1 до 8, записани последователно. "
A14 = " Не знам. "
C = A11
D = A12
A = A13
B = A14
N = random.randint(1, 4)
out_dir = os.path.join(os.getcwd(), "T_J_2_5_1")
os.makedirs(out_dir, exist_ok=True)
aa = " Опишете първия ред и първия стълб на таблицата. \n"
aa2 = " За какво могат да се използват те? "
aa = aa + aa2
img_path = os.path.join(out_dir, "task.png")
point_on_img(img_path)
if not os.path.exists(img_path) or os.path.getsize(img_path) <= 0:
    raise RuntimeError("Task image was not created or is empty: " + img_path)

options = {}
if N == 1:
    answers[k] = "А"
    options = {"А": str(C), "Б": str(A), "В": str(B), "Г": str(D)}
elif N == 2:
    answers[k] = "Б"
    options = {"А": str(A), "Б": str(C), "В": str(B), "Г": str(D)}
elif N == 3:
    answers[k] = "В"
    options = {"А": str(B), "Б": str(A), "В": str(C), "Г": str(D)}
elif N == 4:
    answers[k] = "Г"
    options = {"А": str(D), "Б": str(A), "В": str(B), "Г": str(C)}
ccc = answers[k]
aaa = " Първият ред и първият стълб на таблицата съдържат числата от 1 до 9. "
bbb = " Първият ред и първият стълб на таблицата съдържат числата от 1 до 9. \n"
ddd = " Могат да се използват като номера на съответните редове и стълбове. \n"
ddd1 = " Например редът, който започва с числото 5 е петият ред на таблицата. \n"
ddd2 = " Третият стълб на таблицата е стълбът, който започва с числото 3. "
bbb = bbb + ddd + ddd1 + ddd2

task_uri = encode_png_data_uri(img_path)

payload = {
    "task": aa.strip(),
    "options": options,
    "answer": ccc,
    "hint": aaa,
    "solution": bbb,
    "images": {"task": [task_uri], "solution": []},
}
print(json.dumps(payload, ensure_ascii=False))
