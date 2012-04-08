cd ../data

echo "Extract downloaded dumps"

rm -r -f sql
mkdir sql

echo "Extract category.sql.gz"
gzip -k -d downloads/category.sql.gz
mv downloads/category.sql sql/

echo "Extract categorylinks.sql.gz"
gzip -k -d downloads/categorylinks.sql.gz
mv downloads/categorylinks.sql sql/

echo "Extract page.sql.gz"
gzip -k -d downloads/page.sql.gz
mv downloads/page.sql sql/
