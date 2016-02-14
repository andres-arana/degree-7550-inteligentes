#!/bin/bash
LIST=$@
AMOUNT=0
size=2000000
depthlimit=2
depth=0
categories=''
BASEDIR="/home/Desarrollo/smpiano/proc/"

alias json='python -mjson.tool';


analize_subcategory() {
	local x=$1
	local y=$2
	echo "======= ANALIZING SUB-CATEGORY $x [$y]  - PROFUNDIDAD $depth - FALTAN $AMOUNT =====";
	local body='{"query":{"bool":{"must":[{"term":{"item.category_id":"'$y'"}}],"must_not":[],"should":[]}},"from":0,"size":'$size',"sort":[],"facets":{}}';
	local url='sell-es-entity.ml.com/entity/item/_search';
	resp=$(curl -XPOST $url -d $body);
	echo
	total=$(echo $resp | grep -o '"hits":{"total":[^,]*,' | grep -o '[0-9]*');
	if [ "$total" -eq 0 ] && [ "$depth" -lt "$depthlimit" ]
	then
		run $y
	else
		echo "TOTAL....$total"
		echo "====> GUARDANDO EN ARCHIVO $BASEDIR$categories.txt"
		echo $resp >> "$BASEDIR$categories.txt"
		echo
	fi
	AMOUNT=$((AMOUNT-1))
	echo
	echo
}

analize_category() {
	local x=$1
	echo "===== ANALIZING CATEGORY $x - PROFUNDIDAD $depth - FALTAN $AMOUNT =====";
	echo
	r=$(curl "internal.mercadolibre.com/categories/$x" | grep -o "\"children_categories\":\[\([^]]*\)\]" | sed 's/\(.*\)/{\1}/' | python -mjson.tool);
	echo;
	r2=$(echo -e $r | grep -o "id\": \"[^\"]*\"" | cut -f3 -d\" | tr '\n' ' ');
	local amount=$(echo "$r2" | wc -w)
	AMOUNT=$((AMOUNT-1+amount))
	depth=$((depth+1));
	for y in $r2
	do
		analize_subcategory $x $y
	done
	depth=$((depth-1));
}

push() {
	local separator=''
	if [ ! -z "$categories" ]
	then
		separator='_'
	fi
	categories=$categories$separator$1
}

pop() {
	categories=$(echo $categories | sed 's/_[^_]*$//')
}

run() {
	local amount=$#
	AMOUNT=$((AMOUNT+amount))
	for x in $@
	do
		push $x
		echo "===================> CATEGORIAS --> [$categories]"
		analize_category $x
		pop
	done
}

run $LIST
