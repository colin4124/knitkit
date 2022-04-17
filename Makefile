base_dir = $(abspath .)

DOT := $(shell command -v dot 2> /dev/null)
MILL_BIN := mill
MILL := $(shell command -v mill 2> /dev/null)

$(MILL_BIN):
ifndef MILL
	curl -L https://hub.fastgit.xyz/com-lihaoyi/mill/releases/download/0.10.3/0.10.3 > $@ && chmod +x $@
endif
	ln -s $(shell which mill) mill

build: $(MILL_BIN)
	./mill knitkit.run builds

assembly: $(MILL_BIN)
	./mill knitkit.assembly
	cp $(base_dir)/out/knitkit/assembly.dest/out-tmp.jar $(base_dir)/knitkit.jar

compare:
	python3 scripts/compare.py $(base_dir) doc/compare.yaml
