
.PHONY: default main backup clean mrproper

default: mrproper backup main
	@make clean > /dev/null 2> /dev/null

main:
	@if [[ ! -e main.toc ]]; then pdflatex main.tex; fi
	@pdflatex main.tex

backup:
	@cp main.tex backup/

clean:
	@rm -f main.aux
	@rm -f main.log
	@rm -f main.toc
	@rm -f main.out

mrproper: clean
	@rm -f main.pdf
