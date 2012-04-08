echo "Download latest en wiki dumps"

mkdir ./data/downloads

echo "Download category.sql.gz"
curl http://dumps.wikimedia.org/enwiki/latest/enwiki-latest-category.sql.gz -o ./data/downloads/category.sql.gz

echo "Download categorylinks.sql.gz"
curl http://dumps.wikimedia.org/enwiki/latest/enwiki-latest-categorylinks.sql.gz -o ./data/downloads/categorylinks.sql.gz

echo "Download page.sql.gz"
curl http://dumps.wikimedia.org/enwiki/latest/enwiki-latest-page.sql.gz -o ./data/downloads/page.sql.gz

echo "Download pagelinks.sql.gz"
curl http://dumps.wikimedia.org/enwiki/latest/enwiki-latest-pagelinks.sql.gz -o "./data/downloads/pagelinks.sql.gz"
