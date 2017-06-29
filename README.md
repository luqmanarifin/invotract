# invoice-field-extractor
Extract some known fields of invoices such of organization name, date, invoice amount, using machine learning

## Dependencies

You should have these dependencies to run this project. I only demonstrate installation for Ubuntu, tested on Ubuntu 16.04.
For other operating system, installation will vary.

### Tesseract

Install Tesseract via APT

    sudo apt update sudo apt install tesseract-ocr

Then, clone tessdata project from Tesseract Github repo.
Usually we change directory to `/usr/share/tesseract-ocr`first, then clone the project there

    git clone https://github.com/tesseract-ocr/tessdata

Add the parent directory of this cloned project to `$TESSDATA_PREFIX` environment variable.
Add the `$TESSDATA_PREFIX` with parent directory value to `/etc/environment` so we can get rid of this env variable later.

### Stanford NER Tagger

Download Stanford NER Tagger from this link.

    https://nlp.stanford.edu/software/CRF-NER.shtml#Download

And then unzip. Add this directory after you unzip to `$PATH` environment variable. You can add this path to `/etc/environment`.
Please make sure that you have permission to execute `ner.sh` script, otherwise please `chmod` first.
And make sure `ner.sh` can be called from any directories.

### PDFLib TET

Download from this link

    http://www.pdflib.com/download/tet/

Unzip the file. Then add `<directory you unzip the file>/bin` to `$PATH` environment variable. Make sure that binary file `tet` can be executed from any directories.

## Usage

Open this project in Intellij IDEA. Add JAR dependencies from /lib to the project.
And then build artifact.

You can run the JAR by:

    java -jar invotract.jar