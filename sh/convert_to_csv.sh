echo "Convert to csv"

rm -r -f ./data/csv
mkdir ./data/csv

echo "Convert category.sql"
./sh/sql2csv.sh ./data/sql/category.sql ./data/csv/category.csv

echo "Convert categorylinks.sql"
./sh/sql2csv.sh ./data/sql/categorylinks.sql ./data/csv/categorylinks.csv

echo "Convert page.sql"
./sh/sql2csv.sh ./data/sql/page.sql ./data/csv/page.csv
