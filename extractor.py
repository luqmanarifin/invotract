import sys
import glob
from subprocess import call

print(len(sys.argv))
print(sys.argv)
dir = sys.argv[1]
for f in glob.glob(dir + "/*.pdf"):
    call(["java", "-jar", "out/artifacts/invotract_jar/invotract.jar", f])
