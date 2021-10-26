base_dir = $(abspath .)
compare:
	python3 scripts/compare.py $(base_dir) doc/compare.yaml
