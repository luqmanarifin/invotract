import glob

text = "";
cnt = 0;

for f in glob.glob("*.arff"):
    cnt = cnt + 1
    with open(f, 'rU') as lines:
        for line in lines:
            if line.startswith("@"):
                if cnt == 1:
                    text += line
            else:
                text += line
with open("result.arff", "w") as text_file:
    text_file.write("%s" % (text))
