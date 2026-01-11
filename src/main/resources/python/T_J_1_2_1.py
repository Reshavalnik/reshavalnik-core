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


prime = [2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97]



import random
from fractions import Fraction
from decimal import Decimal
answers = ["А", "А", "А", "А", "А", "А", "А", "А", "А", "А"]
k = 0
K = 6
K2 = 8
DK = [' ', ' ', ' ', ' ', ' ', ' ']
kj = 1
A1 = random.randint(2, 5)
B2 = random.randint(4, 9)
B21 = prime[B2 + 2]
A31 = A1 + 4
D2 = [['когато', 'пелена', 'минута', 'щурвал', 'калмар', 'покана', 'замяна', 'магаре'],
      ['косачка', 'ризница', 'карамел', 'тебешир', 'комисар', 'печалба', 'бонбони', 'касичка'],
      ['наметало', 'каравана', 'балерина', 'колесник', 'биология', 'диплянка', 'каравана','геология'],
      ['нападател', 'колоздач', 'котловина', 'парамедик', 'салатиера', 'залъгалка', 'ковачница', 'карабинер']]
if A1 == 2:
    A11 = []
    A12 = ['карамел']
    A13 = ['колесник']
    A14 = ['парамедик']
    for kk in range(0, K):
        kj = kj + B21
        kj = kj % K2
        DK[kk] = D2[0][kj]
#        print(f"{DK[kk]}")
    kk = B21 % K
    DK[kk] = D2[1][kk]
#    print(f"{DK[kk]}")
    kk = (2 * B21 + 1) % K
    DK[kk] = D2[2][kk]
#    print(f"{DK[kk]}")
    kk = (3 * B21 + 1) % K
    DK[kk] = D2[3][kk]
#    print(f"{DK[kk]}")
    for kk in range(0, K):
        if len(DK[kk]) == A31:
            A11.append(DK[kk])
            A12.append(DK[kk])
            A13.append(DK[kk])
            A14.append(DK[kk])
elif A1 == 3:
    A11 = []
    A12 = ['минута']
    A13 = ['колесник']
    A14 = ['парамедик']
    for kk in range(0, K):
        kj = kj + B21
        kj = kj % K2
        DK[kk] = D2[1][kj]
#        print(f"{DK[kk]}")
    kk = B21 % K
    DK[kk] = D2[0][kk]
#    print(f"{DK[kk]}")
    kk = (2 * B21 + 1) % K
    DK[kk] = D2[2][kk]
#    print(f"{DK[kk]}")
    kk = (3 * B21 + 1) % K
    DK[kk] = D2[3][kk]
#    print(f"{DK[kk]}")
    for kk in range(0, K):
        if len(DK[kk]) == A31:
            A11.append(DK[kk])
            A12.append(DK[kk])
            A13.append(DK[kk])
            A14.append(DK[kk])
elif A1 == 4:
    A11 = []
    A12 = ['минута']
    A13 = ['ризница']
    A14 = ['парамедик']
    for kk in range(0, K):
        kj = kj + B21
        kj = kj % K2
        DK[kk] = D2[2][kj]
#        print(f"{DK[kk]}")
    kk = B21 % K
    DK[kk] = D2[0][kk]
#    print(f"{DK[kk]}")
    kk = (2 * B21 + 1) % K
    DK[kk] = D2[1][kk]
#    print(f"{DK[kk]}")
    kk = (3 * B21 + 1) % K
    DK[kk] = D2[3][kk]
#    print(f"{DK[kk]}")
    for kk in range(0, K):
        if len(DK[kk]) == A31:
            A11.append(DK[kk])
            A12.append(DK[kk])
            A13.append(DK[kk])
            A14.append(DK[kk])
elif A1 == 5:
    A11 = []
    A12 = ['минута']
    A13 = ['ризница']
    A14 = ['колесник']
    for kk in range(0, K):
        kj = kj + B21
        kj = kj % K2
        DK[kk] = D2[3][kj]
#        print(f"{DK[kk]}")
    kk = B21 % K
    DK[kk] = D2[0][kk]
#    print(f"{DK[kk]}")
    kk = (2 * B21 + 1) % K
    DK[kk] = D2[1][kk]
#    print(f"{DK[kk]}")
    kk = (3 * B21 + 1) % K
    DK[kk] = D2[2][kk]
#    print(f"{DK[kk]}")
    for kk in range(0, K):
        if len(DK[kk]) == A31:
            A11.append(DK[kk])
            A12.append(DK[kk])
            A13.append(DK[kk])
            A14.append(DK[kk])
C = A11
D = A12
A = A13
B = A14
N = random.randint(1, 4)
directoryPath = 'C:/latex/im_21/'
taskName = 'T_J_1_2_1'
imagesIndex = str(A1) + '_' + str(B2) + '_' + str(N) + '.png'
if not isdir(directoryPath + taskName): mkdir(directoryPath + taskName)
aa = " Изберете думите, съставени от " + str(A31) + " букви. \n"
aa2 = str(DK)
aa = aa + aa2

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
aaa = " Пребройте буквите във всяка от дадените думи. "
bb = DK[0]
ba = len(bb)
bbb = " Думата '" + str(bb) + "' се състои от " + str(ba) + " букви. \n"
if ba == A31:
    ddd = " Добавяме я в списъка. \n"
elif ba != A31:
    ddd = " Пропускаме тази дума. \n"
bb = DK[1]
ba = len(bb)
ddd1 = " Думата '" + str(bb) + "' се състои от " + str(ba) + " букви. \n"
if ba == A31:
    ddd2 = " Добавяме я в списъка. \n"
elif ba != A31:
    ddd2 = " Пропускаме тази дума. \n"
bb = DK[2]
ba = len(bb)
ddd3 = " Думата '" + str(bb) + "' се състои от " + str(ba) + " букви. \n"
if ba == A31:
    ddd4 = " Добавяме я в списъка. \n"
elif ba != A31:
    ddd4 = " Пропускаме тази дума. \n"
bb = DK[3]
ba = len(bb)
ddd5 = " Думата '" + str(bb) + "' се състои от " + str(ba) + " букви. \n"
if ba == A31:
    ddd6 = " Добавяме я в списъка. \n"
elif ba != A31:
    ddd6 = " Пропускаме тази дума. \n"
bb = DK[4]
ba = len(bb)
ddd7 = " Думата '" + str(bb) + "' се състои от " + str(ba) + " букви. \n"
if ba == A31:
    ddd8 = " Добавяме я в списъка. \n"
elif ba != A31:
    ddd8 = " Пропускаме тази дума. \n"
bb = DK[5]
ba = len(bb)
ddd9 = " Думата '" + str(bb) + "' се състои от " + str(ba) + " букви. \n"
if ba == A31:
    ddd10 = " Добавяме я в списъка. \n"
elif ba != A31:
    ddd10 = " Пропускаме тази дума. \n"
ddd11 = " Следователно думите са: " + str(A11) + ". "
bbb = bbb + ddd + ddd1 + ddd2 + ddd3 + ddd4 + ddd5 + ddd6 + ddd7 + ddd8 + ddd9 + ddd10 + ddd11

payload = {
    "task": aa.strip(),
    "options": options,
    "answer": ccc,
    "hint": aaa,
    "solution": bbb,
    "images": {"task": [], "solution": []},
}
print(json.dumps(payload, ensure_ascii=False))
