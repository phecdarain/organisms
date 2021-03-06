###########################################################
#
# Makefile for LaTeX docs
#
# $Id: 
#

.SUFFIXES:
.SUFFIXES: .ps .dvi .tex

TEXINPUTS := .:${TEXINPUTS}
BIBINPUTS := .:${TEXINPUTS}

#all: eps dvi ps pdf # res
all: pdf
eps: fig obj

PAPER=reportGroup5

LATEX=pdflatex

ps:  $(PAPER).ps

pdf: $(PAPER).pdf
dvi: $(PAPER).dvi

obj: $(patsubst %.obj, %.eps, $(wildcard *.obj))
fig: $(patsubst %.fig, %.eps, $(wildcard *.fig))

res:
	make -C results
	make -C figs

clean:
	$(RM) *.aux *.log *.bbl *.blg *~ \#*\# *.toc *.idx *.out
	$(RM) $(patsubst %.tex, %.ps, $(wildcard *.tex))
	$(RM) $(patsubst %.tex, %.dvi, $(wildcard *.tex))
	$(RM) $(patsubst %.tex, %.pdf, $(wildcard *.tex))

distclean: clean
	$(RM) $(patsubst %.fig, %.eps, $(wildcard *.fig))
	$(RM) $(patsubst %.obj, %.eps, $(wildcard *.obj))

# only need -Ppdf if using CM fonts
#%.pdf: %.dvi $(wildcard *.bib)
#	dvips -Ppdf -G0 -t letter -o $*.tmp.ps $<
#	ps2pdf $*.tmp.ps $*.pdf
#	$(RM) $*.tmp.ps $*.dvi *.aux *.log *.bbl *.blg *.toc

%.pdf: $(wildcard *.tex) $(wildcard *.bib)
	$(LATEX) $*
	#bibtex $*
	$(LATEX) $*
	if [ -e $*.toc ] ; then $(LATEX) $* ; fi
	if [ -e $*.bbl ] ; then $(LATEX) $* ; fi
	if egrep Rerun $*.log ; then $(LATEX) $* ; fi
	if egrep Rerun $*.log ; then $(LATEX) $* ; fi
	if egrep Rerun $*.log ; then $(LATEX) $* ; fi
	#dvips -t letter -z -Ppdf -G0 -D600 main.dvi -o main.ps
	#ps2pdf main.ps main.pdf

	$(RM) *.aux *.log *.bbl *.blg *.toc *.out

%.ps: %.dvi
	dvips -Ppdf -G0 -tletter -o $@ $<
%.ps.gz: %.ps
	gzip -v9 $<

%.eps: %.obj
	tgif -print -eps $*

%.eps: %.fig
	fig2dev -L eps $< $@
