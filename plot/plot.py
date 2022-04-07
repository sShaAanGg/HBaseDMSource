import numpy as np
import matplotlib.pyplot as plt

f = open("myfile.txt", "r", encoding="utf-8")

x = np.arange(0, 10, 0.1)
y = np.sin(x)
plt.plot(x, y)
plt.savefig("fig.png")
