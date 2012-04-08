cd ../data

echo "Convert to csv"

rm -r -f csv
mkdir csv

echo "Convert category.sql"
../sh/sql2csv.sh sql/category.sql csv/category.csv

echo "Convert categorylinks.sql"
../sh/sql2csv.sh sql/categorylinks.sql csv/categorylinks.csv

echo "Convert page.sql"
../sh/sql2csv.sh sql/page.sql csv/page.csv
