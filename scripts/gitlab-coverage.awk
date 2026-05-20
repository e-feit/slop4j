BEGIN {
  FS = ","
}

FNR == 1 {
  next
}

{
  missed += $8
  covered += $9
}

END {
  total = missed + covered
  if (total == 0) {
    print "Total line coverage: 0.00%"
  } else {
    printf "Total line coverage: %.2f%%\n", 100 * covered / total
  }
}
