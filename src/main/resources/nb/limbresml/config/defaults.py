from .config import CfgNode as CN

_C = CN()
_C.MODEL = "NB"
_C.TUNE_HP_PARAMS = False

# -----------------------------------------------------------------------------
# Input
# -----------------------------------------------------------------------------
_C.INPUT = CN()
_C.INPUT.PATH = "data/ns10_ls300_normalized.npz"
_C.INPUT.CAT_TRAIN_VAL = True

# -----------------------------------------------------------------------------
# Output
# -----------------------------------------------------------------------------
_C.OUTPUT_DIR = "./output"
