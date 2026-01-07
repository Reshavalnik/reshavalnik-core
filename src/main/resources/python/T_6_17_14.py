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


def encode_png_base64(path):
    if not path:
        return None
    try:
        with open(path, "rb") as image_file:
            return base64.b64encode(image_file.read()).decode("ascii")
    except Exception:
        return None


def point_on_img(filename='01.png', F1=1, D=1, D2=1):
    # prepare Width
    w_image = 800
    h_image = w_image
    if F1 == 1:
        X = 400 + D
        Y = 400 - D2
    elif F1 == 2:
        X = 400 - D
        Y = 400 - D2
    elif F1 == 3:
        X = 400 - D
        Y = 400 + D2
    elif F1 == 4:
        X = 400 + D
        Y = 400 + D2
    X1 = X + 10
    Y1 = Y + 10
    # margin = size * 2  # 1 * letter distace from the image edge
    # h_text = h_image-(2*margin)
    # w_text = w_image - (margin)
    #	lines = textwrap.wrap(text, 2 * w_text / size)
    #	font = ImageFont.truetype('arial.ttf', size)
    #	letterWidth, letterHeight = font.getsize(lines.__getitem__(0))
    # prepare Height
    # h_start = 10
    #	h_image = letterHeight * len(lines) + 2*h_start
    # w_start = 10
    FOREGROUND = (0, 0, 255)
    # create image
    image1 = Image.new(mode="RGB", size=(w_image, h_image), color="black")
    #	image2 = Image.new(mode="RGB", size=(w_image, h_image), color="black")
    draw = ImageDraw.Draw(image1)
    draw.line((10, 400, 795, 400), fill=(255, 255, 255), width=1)
    draw.line((400, 10, 400, 790), fill=(255, 255, 255), width=1)
    draw.line((10, 50, 795, 50), fill=(0, 0, 255), width=1)
    draw.line((10, 100, 795, 100), fill=(0, 0, 255), width=1)
    draw.line((10, 150, 795, 150), fill=(0, 0, 255), width=1)
    draw.line((10, 200, 795, 200), fill=(0, 0, 255), width=1)
    draw.line((10, 250, 795, 250), fill=(0, 0, 255), width=1)
    draw.line((10, 300, 795, 300), fill=(0, 0, 255), width=1)
    draw.line((10, 350, 795, 350), fill=(0, 0, 255), width=1)
    draw.line((10, 450, 795, 450), fill=(0, 0, 255), width=1)
    draw.line((10, 500, 795, 500), fill=(0, 0, 255), width=1)
    draw.line((10, 550, 795, 550), fill=(0, 0, 255), width=1)
    draw.line((10, 600, 795, 600), fill=(0, 0, 255), width=1)
    draw.line((10, 650, 795, 650), fill=(0, 0, 255), width=1)
    draw.line((10, 700, 795, 700), fill=(0, 0, 255), width=1)
    draw.line((10, 750, 795, 750), fill=(0, 0, 255), width=1)
    #
    draw.line((50, 10, 50, 790), fill=(0, 0, 255), width=1)
    draw.line((100, 10, 100, 790), fill=(0, 0, 255), width=1)
    draw.line((150, 10, 150, 790), fill=(0, 0, 255), width=1)
    draw.line((200, 10, 200, 790), fill=(0, 0, 255), width=1)
    draw.line((250, 10, 250, 790), fill=(0, 0, 255), width=1)
    draw.line((300, 10, 300, 790), fill=(0, 0, 255), width=1)
    draw.line((350, 10, 350, 790), fill=(0, 0, 255), width=1)
    draw.line((450, 10, 450, 790), fill=(0, 0, 255), width=1)
    draw.line((500, 10, 500, 790), fill=(0, 0, 255), width=1)
    draw.line((550, 10, 550, 790), fill=(0, 0, 255), width=1)
    draw.line((600, 10, 600, 790), fill=(0, 0, 255), width=1)
    draw.line((650, 10, 650, 790), fill=(0, 0, 255), width=1)
    draw.line((700, 10, 700, 790), fill=(0, 0, 255), width=1)
    draw.line((750, 10, 750, 790), fill=(0, 0, 255), width=1)
    #	draw.line((310, 105, 595, 105), fill=(255, 255, 0), width=1)
    draw.ellipse((X, Y, X1, Y1), fill=(255, 0, 0), outline=(0, 0, 0), width=1)
    #	draw.ellipse((300, 100, 310, 110), fill=(255, 0, 0), outline=(0, 0, 0), width=1)
    #	draw.ellipse((600, 100, 610, 110), fill=(255, 0, 0), outline=(0, 0, 0), width=1)
    #	draw.line((110, 105, 295, 105), fill=(255, 255, 0), width=1)
    #	draw.line((310, 105, 595, 105), fill=(255, 255, 0), width=1)
    I1 = ImageDraw.Draw(image1)
    size = 20
    font = load_font(size)
    draw.text((X1, Y1), 'A', font=font, fill=(255, 255, 255))
    #	draw.text((300, 110), 'B', font=font, fill=(255, 255, 255))
    #	draw.text((600, 110), 'C', font=font, fill=(255, 255, 255))
    #	draw.text((450, 110), 'D', font=font, fill=(255, 255, 255))
    #	draw.text((650, 110), 'E', font=font, fill=(255, 255, 255))
    #	draw = ImageDraw.Draw(image2)
    #	draw.ellipse((100, 100, 110, 110), fill=(255, 0, 0), outline=(0, 0, 0), width=1)
    #	draw.ellipse((200, 200, 210, 210), fill=(255, 0, 0), outline=(0, 0, 0), width=1)
    # y_text = h_start
    # draw.text((10,10), text, font=font, fill=FOREGROUND)
    # save file
    image1.save(filename)
    return filename


def spoint_on_img(filename='01.png', F1=1, D=1, D2=1):
    # prepare Width
    w_image = 800
    h_image = w_image
    if F1 == 1:
        X = 400 + D
        Y = 400 - D2
    elif F1 == 2:
        X = 400 - D
        Y = 400 - D2
    elif F1 == 3:
        X = 400 - D
        Y = 400 + D2
    elif F1 == 4:
        X = 400 + D
        Y = 400 + D2
    X1 = X + 10
    Y1 = Y + 10
    # margin = size * 2  # 1 * letter distace from the image edge
    # h_text = h_image-(2*margin)
    # w_text = w_image - (margin)
    #	lines = textwrap.wrap(text, 2 * w_text / size)
    #	font = ImageFont.truetype('arial.ttf', size)
    #	letterWidth, letterHeight = font.getsize(lines.__getitem__(0))
    # prepare Height
    # h_start = 10
    #	h_image = letterHeight * len(lines) + 2*h_start
    # w_start = 10
    FOREGROUND = (0, 0, 255)
    # create image
    image1 = Image.new(mode="RGB", size=(w_image, h_image), color="black")
    #	image2 = Image.new(mode="RGB", size=(w_image, h_image), color="black")
    draw = ImageDraw.Draw(image1)
    draw.line((10, 400, 795, 400), fill=(255, 255, 255), width=1)
    draw.line((400, 10, 400, 790), fill=(255, 255, 255), width=1)
    draw.line((10, 50, 795, 50), fill=(0, 0, 255), width=1)
    draw.line((10, 100, 795, 100), fill=(0, 0, 255), width=1)
    draw.line((10, 150, 795, 150), fill=(0, 0, 255), width=1)
    draw.line((10, 200, 795, 200), fill=(0, 0, 255), width=1)
    draw.line((10, 250, 795, 250), fill=(0, 0, 255), width=1)
    draw.line((10, 300, 795, 300), fill=(0, 0, 255), width=1)
    draw.line((10, 350, 795, 350), fill=(0, 0, 255), width=1)
    draw.line((10, 450, 795, 450), fill=(0, 0, 255), width=1)
    draw.line((10, 500, 795, 500), fill=(0, 0, 255), width=1)
    draw.line((10, 550, 795, 550), fill=(0, 0, 255), width=1)
    draw.line((10, 600, 795, 600), fill=(0, 0, 255), width=1)
    draw.line((10, 650, 795, 650), fill=(0, 0, 255), width=1)
    draw.line((10, 700, 795, 700), fill=(0, 0, 255), width=1)
    draw.line((10, 750, 795, 750), fill=(0, 0, 255), width=1)
    #
    draw.line((50, 10, 50, 790), fill=(0, 0, 255), width=1)
    draw.line((100, 10, 100, 790), fill=(0, 0, 255), width=1)
    draw.line((150, 10, 150, 790), fill=(0, 0, 255), width=1)
    draw.line((200, 10, 200, 790), fill=(0, 0, 255), width=1)
    draw.line((250, 10, 250, 790), fill=(0, 0, 255), width=1)
    draw.line((300, 10, 300, 790), fill=(0, 0, 255), width=1)
    draw.line((350, 10, 350, 790), fill=(0, 0, 255), width=1)
    draw.line((450, 10, 450, 790), fill=(0, 0, 255), width=1)
    draw.line((500, 10, 500, 790), fill=(0, 0, 255), width=1)
    draw.line((550, 10, 550, 790), fill=(0, 0, 255), width=1)
    draw.line((600, 10, 600, 790), fill=(0, 0, 255), width=1)
    draw.line((650, 10, 650, 790), fill=(0, 0, 255), width=1)
    draw.line((700, 10, 700, 790), fill=(0, 0, 255), width=1)
    draw.line((750, 10, 750, 790), fill=(0, 0, 255), width=1)
    #	draw.line((310, 105, 595, 105), fill=(255, 255, 0), width=1)
    draw.ellipse((X, Y, X1, Y1), fill=(255, 0, 0), outline=(0, 0, 0), width=1)
    Y = 800 - Y
    draw.ellipse((X, Y, X1, Y + 10), fill=(255, 255, 0), outline=(0, 0, 0), width=1)
    #	draw.ellipse((300, 100, 310, 110), fill=(255, 0, 0), outline=(0, 0, 0), width=1)
    #	draw.ellipse((600, 100, 610, 110), fill=(255, 0, 0), outline=(0, 0, 0), width=1)
    #	draw.line((110, 105, 295, 105), fill=(255, 255, 0), width=1)
    #	draw.line((310, 105, 595, 105), fill=(255, 255, 0), width=1)
    I1 = ImageDraw.Draw(image1)
    size = 20
    font = load_font(size)
    draw.text((X1, Y1), 'A', font=font, fill=(255, 255, 255))
    #	draw.text((300, 110), 'B', font=font, fill=(255, 255, 255))
    #	draw.text((600, 110), 'C', font=font, fill=(255, 255, 255))
    #	draw.text((450, 110), 'D', font=font, fill=(255, 255, 255))
    #	draw.text((650, 110), 'E', font=font, fill=(255, 255, 255))
    #	draw = ImageDraw.Draw(image2)
    #	draw.ellipse((100, 100, 110, 110), fill=(255, 0, 0), outline=(0, 0, 0), width=1)
    #	draw.ellipse((200, 200, 210, 210), fill=(255, 0, 0), outline=(0, 0, 0), width=1)
    # y_text = h_start
    # draw.text((10,10), text, font=font, fill=FOREGROUND)
    # save file
    image1.save(filename)
    return filename



import random
from decimal import Decimal
from fractions import Fraction
answers = ["А", "А", "А", "А", "А", "А", "А", "А", "А", "А"]
k = 3
D1 = random.randint(1, 4)
D2 = random.randint(1, 6)
D3 = random.randint(1, 7)
D21 = D2 + 1
D20 = D2 * 50
D30 = D3 * 50
directoryPath = 'C:/latex/im_21/'
taskName = 'T_6_17_14'
N = random.randint(1, 4)
imagesIndex = str(D1) + '_' + str(D2) + '_' + str(D3) + '_' + str(N) + '.png'
if not isdir(directoryPath + taskName): mkdir(directoryPath + taskName)
task_image_path = point_on_img(directoryPath + taskName + '/imq1' + imagesIndex, D1, D20, D30)
if D1 == 1:
    B1 = " (" + str(D2) + ",-" + str(D3) + ") "
    B2 = " (" + str(D21) + "," + str(D3) + ") "
    B3 = " (0," + str(D2) + ")"
    B4 = " (" + str(D3) + "," + str(D1) + ") "
#   tst17_1_A(D1, D2, D3)
elif D1 == 2:
    B1 = " (" + str(D2) + ",-" + str(D3) + ") "
    B2 = " (" + str(D21) + "," + str(D3) + ") "
    B3 = " (0," + str(D2) + ")"
    B4 = " (-" + str(D3) + "," + str(D2) + ") "
#   tst17_1_A(D1, D2, D3)
elif D1 == 3:
    B1 = " (-" + str(D2) + "," + str(D3) + ") "
    B2 = " (" + str(D21) + "," + str(D3) + ") "
    B3 = " (0," + str(D2) + ")"
    B4 = " (" + str(D3) + "," + str(D2) + ") "
#   tst17_1_A(D1, D2, D3)
elif D1 == 4:
    B1 = " (" + str(D2) + "," + str(D3) + ") "
    B2 = " (" + str(D21) + ",-" + str(D3) + ") "
    B3 = " (0," + str(D2) + ")"
    B4 = " (" + str(D3) + ",-" + str(D2) + ") "
#   tst17_1_A(D1, D2, D3)
C = B1
D = B2
A = B3
B = B4
aa = " Симетричната на точка А на чертежа спрямо оста Ох има координати: "
if N == 1:
    answers[k] = "А"
    Sta = ' А) ' + str(C)
    Stb = ' Б) ' + str(A)
    Stc = ' В) ' + str(B)
    Std = ' Г) ' + str(D)
    options = {"А": str(C), "Б": str(A), "В": str(B), "Г": str(D)}
elif N == 2:
    answers[k] = "Б"
    Sta = ' А) ' + str(A)
    Stb = ' Б) ' + str(C)
    Stc = ' В) ' + str(B)
    Std = ' Г) ' + str(D)
    options = {"А": str(A), "Б": str(C), "В": str(B), "Г": str(D)}
elif N == 3:
    answers[k] = "В"
    Sta = ' А) ' + str(B)
    Stb = ' Б) ' + str(A)
    Stc = ' В) ' + str(C)
    Std = ' Г) ' + str(D)
    options = {"А": str(B), "Б": str(A), "В": str(C), "Г": str(D)}
elif N == 4:
    answers[k] = "Г"
    Sta = ' А) ' + str(D)
    Stb = ' Б) ' + str(A)
    Stc = ' В) ' + str(B)
    Std = ' Г) ' + str(C)
    options = {"А": str(D), "Б": str(A), "В": str(B), "Г": str(C)}
answer = answers[k]
aaa = " Разгледайте знаците на координатите. "
if D1 == 1 or D1 == 2:
    bbb = " точката А е с положителна ордината. Симетричната на А спрямо абцисната ос, ще има ордината -" + str(D3) + ".\n"
    ddd = " Симетричната на А спрямо абцисната ос, ще има същата абциса. \n"
    ddd1 = " Симетричната на точката А има координати (" + str(D2) + ", -" + str(D3) + "). "
    bbb = bbb + ddd + ddd1
    solution_image_path = spoint_on_img(directoryPath + taskName + '/ims1' + imagesIndex, D1, D20, D30)
elif D1 == 3 or D1 == 4:
    bbb = " точката А е с отрицателна ордината. Симетричната на А спрямо абцисната ос, ще има ордината " + str(D3) + ".\n"
    ddd = " Симетричната на А спрямо абцисната ос, ще има същата абциса. \n"
    ddd1 = " Симетричната на точката А има координати (" + str(D2) + "," + str(D3) + "). "
    bbb = bbb + ddd + ddd1
    solution_image_path = spoint_on_img(directoryPath + taskName + '/ims1' + imagesIndex, D1, D20, D30)

images = []
task_base64 = encode_png_base64(task_image_path)
if task_base64:
    images.append({"kind": "TASK", "mime": "image/png", "base64": task_base64})
solution_base64 = encode_png_base64(solution_image_path)
if solution_base64:
    images.append({"kind": "SOLUTION", "mime": "image/png", "base64": solution_base64})

payload = {
    "task": aa.strip(),
    "options": options,
    "answer": answer,
    "hint": aaa,
    "solution": bbb,
    "images": images,
}
print(json.dumps(payload, ensure_ascii=False))
