#!/bin/sh

gpl="$(cat GPL)"
gpl_cutoff=$(echo "$gpl" | wc -l)
bsd="$(sed \$d < LICENSE)"

for java in $(git ls-files \*.java | grep -ve algorithm -e LOCI)
do
	top="$(sed ${gpl_cutoff}q < $java)"
	has_gpl=t
	if test "$gpl" != "$top"
	then
		echo "$top" > .tmp
		if test 2 -lt $(git diff --no-index -w GPL .tmp | wc -l)
		then
			has_gpl=f
		fi
	fi
	author="Stephan Preibisch & Stephan Saalfeld"
	cutoff=$gpl_cutoff
	end_comment=
	case $has_gpl in
	t)
		cutoff=$(($cutoff+1))
		line="$(sed -n ${cutoff}'s/^ \* @author \(.*\)$/\1/p' \
			< $java)"
		if test ! -z "$line"
		then
			author="$line"
		fi
		;;
	f)
		case "$top" in
		package*)
			cutoff=0
			end_comment=' */\n'
			;;
		*)
			echo "error: $java does not have a GPL header"
			git diff --color --no-index -w GPL .tmp
			continue
			;;
		esac
	esac
	escaped="$(echo "$author" | sed 's/&/\\&/g')"
	(echo "$bsd" |
		sed "s/Stephan Preibisch & Stephan Saalfeld/$escaped/" &&
		printf " *\n * @author %s\n$end_comment" "$author" &&
		if test $cutoff -gt 0
		then
			sed 1,${cutoff}d < $java
		else
			cat $java
		fi) > $java.new &&
	mv $java.new $java
done | less -FRSX
