rm -r -f ./data/preprocessed
mkdir ./data/preprocessed

echo "Filter pages (I need only main and category pages)"
java -cp "./out/artifacts/Diploma_jar/Diploma.jar" ru.abishev.Runner filter_pages "./data/csv/page.csv" "./data/preprocessed/page.csv"

echo "Preprocess categories (I dont need categories from 2012, and without page info)"
java -cp "./out/artifacts/Diploma_jar/Diploma.jar" ru.abishev.Runner preprocess_categories "./data/csv/category.csv" "./data/preprocessed/category.csv"

echo "Preprocess categorylinks (Sync with categories)"
java -cp "./out/artifacts/Diploma_jar/Diploma.jar" ru.abishev.Runner preprocess_categorylinks "./data/csv/categorylinks.csv" "./data/preprocessed/categorylinks.csv"


