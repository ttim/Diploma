echo "Extract downloaded dumps"

rm -r -f ./data/sql
mkdir ./data/sql

echo "Extract category.sql.gz"
gzip -k -d ./data/downloads/category.sql.gz
mv ./data/downloads/category.sql ./data/sql/

echo "Extract categorylinks.sql.gz"
gzip -k -d ./data/downloads/categorylinks.sql.gz
mv ./data/downloads/categorylinks.sql ./data/sql/

echo "Extract page.sql.gz"
gzip -k -d ./data/downloads/page.sql.gz
mv ./data/downloads/page.sql ./data/sql/

echo "Extract pagelinks.sql.gz"
gzip -k -d ./data/downloads/pagelinks.sql.gz
mv ./data/downloads/pagelinks.sql ./data/sql/
