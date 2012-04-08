rm -r -f ./data/preprocessed
mkdir ./data/preprocessed

echo "Filter pages (I need only main and category pages)"
java -cp "./out/artifacts/Diploma_jar/Diploma.jar" ru.abishev.Runner filter_pages "./data/csv/page.csv" "./data/preprocessed/page.csv"

echo "Preprocess categories (I dont need categories from 2012, and without page info)"
java -cp "./out/artifacts/Diploma_jar/Diploma.jar" ru.abishev.Runner preprocess_categories "./data/csv/category.csv" "./data/preprocessed/category.csv"

echo "Preprocess categorylinks (Sync with categories)"
java -cp "./out/artifacts/Diploma_jar/Diploma.jar" ru.abishev.Runner preprocess_categorylinks "./data/csv/categorylinks.csv" "./data/preprocessed/categorylinks.csv"

echo "Divide categorylinks to 3 parts (files, subcats, pages)"
echo "Files"
grep "file" ./data/preprocessed/categorylinks.csv > ./data/preprocessed/categorylinks_files.csv
echo "Pages"
grep "page" ./data/preprocessed/categorylinks.csv > ./data/preprocessed/categorylinks_pages.csv
echo "Subcats"
grep "subcat" ./data/preprocessed/categorylinks.csv > ./data/preprocessed/categorylinks_subcats.csv

echo "Filter pagelinks (I need only links from main to main)"
java -cp "./out/artifacts/Diploma_jar/Diploma.jar" ru.abishev.Runner filter_pagelinks "./data/csv/pagelinks.csv" "./data/preprocessed/pagelinks.csv"
