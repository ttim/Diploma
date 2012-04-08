cd ../data

echo "Download latest en wiki dumps"

mkdir downloads

echo "Download category.sql.gz"
curl http://dumps.wikimedia.org/enwiki/latest/enwiki-latest-category.sql.gz -o downloads/category.sql.gz

echo "Download categorylinks.sql.gz"
curl http://dumps.wikimedia.org/enwiki/latest/enwiki-latest-categorylinks.sql.gz -o downloads/categorylinks.sql.gz

echo "Download page.sql.gz"
curl http://dumps.wikimedia.org/enwiki/latest/enwiki-latest-page.sql.gz -o downloads/page.sql.gz
