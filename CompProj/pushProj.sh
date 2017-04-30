Comment=$1
rm *.class */*.class */*/*.class
git pull
git add -A
echo "Commiting $1"
git commit -m "$Comment"
git push
