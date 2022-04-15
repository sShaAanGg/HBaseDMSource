import numpy as np
import matplotlib.pyplot as plt

f = open("../output/output1.txt", "r", encoding="utf-8")
line_list = f.read().split('\n')
str_list1 = line_list[0].split(' ')
str_list2 = line_list[1].split(' ')
# print(line_list)

x1 = str_list1[2]
x2 = str_list2[2]
print(x1, x2)
# y = np.sin(x)
# plt.plot(x, y)
# plt.savefig("fig.png")
