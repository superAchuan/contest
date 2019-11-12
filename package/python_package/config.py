import os
class DefaultConfigs(object):
    # hardware
    gpu = "0"

    # numerical configs
    NB_IV3_LAYERS_TO_FREEZE = 172
    image_size = 299
    batch_size = 32
    # nb_classes = 2
    RGB = True
    lr = 0.0001
    # epochs = 1

    FC_SIZE = 1024
    dropout = 0.3
    val_size = 0.2


    # test modify
    epochs = 4
    nb_classes = 100
    # train_dir = "/home/yangchuan/imageseg/errors/1"
    train_dir = os.environ['IMAGE_TRAIN_INPUT_PATH']
    save_model_url = '/saved_model/test.h5'
    model_url = ''
    # save_model_pb = './saved_model'
    save_model_pb = os.environ['MODEL_INFERENCE_PATH']



config = DefaultConfigs()
