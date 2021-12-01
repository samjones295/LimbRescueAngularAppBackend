cd mlp/
python3 generate_dataset.py --data-dir rawdata/files --anno-file rawdata/annotations.csv --save-path data/ns10_ls300_normalized_switch.npz
python3 train_net.py