from __future__ import print_function
import keras
from keras.datasets import cifar10
import tensorflow as tf
from tensorflow.python.keras.preprocessing.image import ImageDataGenerator
from tensorflow.python.keras.models import Sequential
from tensorflow.python.keras.layers import Dense, Dropout, Activation, Flatten
from tensorflow.python.keras.layers import Conv2D, MaxPooling2D, ZeroPadding2D, GlobalMaxPooling2D
from tensorflow.python.keras.optimizers import SGD, Adam, RMSprop
from tensorflow.python.keras.callbacks import TensorBoard
from tensorflow.python.keras.applications.inception_resnet_v2 import InceptionResNetV2
from tensorflow.python.keras.models import Model
from tensorflow.python.keras.layers import Dense,Flatten,Dropout, MaxPooling2D,Conv2D,Input,GlobalAveragePooling2D
from tensorflow.python.keras.models import load_model
import cv2
import numpy as np
from config import config
import os
from tensorflow.python.keras.callbacks import ModelCheckpoint
from tqdm import tqdm

from tensorflow.python.keras.applications.densenet import DenseNet121
from tensorflow.python.keras.applications.densenet import DenseNet201
from tensorflow.python.keras.applications.xception import Xception


#load dataset
# (x_train,y_train),(x_test,y_test)= cifar10.load_data()
os.environ["CUDA_DEVICE_ORDER"] = "PCI_BUS_ID"
# os.environ["CUDA_VISIBLE_DEVICES"] = config.gpu
# os.environ["CUDA_VISIBLE_DEVICES"] = "3"
image_size = config.image_size


def add_new_classifier(base_model):
    FC_SIZE = config.FC_SIZE
    # FC_SIZE = 512
    n_classes = config.nb_classes
    x = base_model.output
    x = GlobalAveragePooling2D()(x)
    x = Dropout(config.dropout)(x)
    x = Dense(FC_SIZE, activation='relu')(x)
    predictions = Dense(n_classes, activation='softmax')(x)
    model = Model(inputs=base_model.input, outputs=predictions)
    return model

if __name__ == '__main__':
    # Hyperparameter setting
    batch_size = config.batch_size
    num_classes = config.nb_classes
    epochs = config.epochs
    # Get the path of current running file
    pwd = os.path.dirname(os.path.abspath(__file__))

    # Construct Xception model with our own top
    base_model = Xception(include_top=False,
                          weights=pwd + '/xception_weights_tf_dim_ordering_tf_kernels_notop.h5',
                          input_shape=(image_size, image_size, 3))
    model = add_new_classifier(base_model)
    # Unfreeze all layers
    # for layer in base_model.layers:
    #     layer.trainable = False
    print("Xception!!!")
    # print(model.summary())

    # compile model
    model.compile(loss='categorical_crossentropy', optimizer=Adam(lr=config.lr, decay=1e-6), metrics=['accuracy'])

    # Data Augmentation:
    datagen = ImageDataGenerator(
        rescale=1/255.0,
        featurewise_center=False,  # set input mean to 0 over the dataset
        samplewise_center=False,  # set each sample mean to 0
        featurewise_std_normalization=False,  # divide inputs by std of the dataset
        samplewise_std_normalization=False,  # divide each input by its std
        zca_whitening=False,  # apply ZCA whitening
        rotation_range=30,  # randomly rotate images in the range (degrees, 0 to 180)
        width_shift_range=0.2,  # randomly shift images horizontally (fraction of total width)
        height_shift_range=0.2,  # randomly shift images vertically (fraction of total height)
        horizontal_flip=True,  # randomly flip images
        vertical_flip=False,    # randomly flip images
        )

    # Put all images into CNN to train the model
    train_dir = config.train_dir
    print(os.listdir(train_dir))
    train_generator = datagen.flow_from_directory(train_dir,
                                                  target_size=(image_size, image_size),
                                                  batch_size=batch_size,
                                                  shuffle=True,
                                                  seed=666,
                                                  # classes=dirs,
                                                  class_mode='categorical',
                                                  )
    # train model
    model.fit_generator(train_generator,
                        steps_per_epoch=train_generator.samples // batch_size,
                        epochs=epochs
                        )

    # save model to specified path with specified format
    MODEL_PATH = config.save_model_pb
    tf.keras.experimental.export_saved_model(model, MODEL_PATH + '/SavedModel')






