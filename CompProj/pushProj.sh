Comment=$1
git pull
git add -A
echo "Commiting $1"
git commit -m "$Comment"
git push
