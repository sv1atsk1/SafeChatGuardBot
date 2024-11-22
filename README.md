# Setting Up and Running the Server

## Requirements

To run the server, you will need the following dependencies:

- **openpy**: `4.10.0.84`  
- **torch**: `2.3.0`  
- **transformers**: `4.45.1`  
- **numpy**: `1.26.4`  
- **pycuda**: `2024.1`  
- **fastapi**: `0.115.0`  
- **python**: `3.12.3`  
- **onnx**: `1.16.1`  
- **onnxruntime-gpu**: `1.18.0`  
- **onnxscript**: `0.1.0.dev20240526`  

---

## Preparing Models and Prerequisites

Before running the server, you need to convert the models to the **ONNX TensorRT** format.  
To do this, ensure the following software is installed on your system:

- **CUDA 11.8**  
- **TensorRT 10.1**

### Steps for Installing TensorRT:

1. Download and install TensorRT to the following directory:

 ```bash
 C:\Install_Path\TensorRT-10.1.0.6\
 ```
   
3. Navigate to the Python installation path for version 3.12 and install the required Python package:

```bash
pip install pycuda
```

3. For running models, install pycuda (which requires a C++ compiler).
On Windows 10, you may use Microsoft Visual Studio with the appropriate C++ compiler package.

## Once all dependencies are installed, the model conversion process can be executed using the following console commands.

## ONNX Models
Pre-trained ONNX models can be downloaded from the link below:
https://drive.google.com/drive/folders/1UxHfqFipHKOP0EPgwP4XkY7X5VAe3Czw?usp=sharing
https://drive.google.com/drive/folders/1FNb8b_ZvAOODUPAhP5qxkAb2ik_Byiem?usp=sharing

## Converting Models to TRT Format
Commands for Conversion:
NSFW Model Conversion:
```bash
trtexec --onnx=nsfw_cls.onnx --best --saveEngine=nsfw_cls_best.trt --builderOptimizationLevel=5 --iterations=100 --warmUp=10000 --duration=10 --useCudaGraph
```
Text Inappropriateness Detection:
```bash
trtexec --onnx=text_inappropriate.onnx --fp16 --saveEngine=text_inappropriate_fp16.trt --builderOptimizationLevel=5 --iterations=100 --warmUp=10000 --duration=10 --useCudaGraph
```
Text Classification:
```bash
trtexec --onnx=text_type_classification.onnx --fp16 --saveEngine=text_type_classification_fp16.trt --builderOptimizationLevel=5 --iterations=100 --warmUp=10000 --duration=10 --useCudaGraph
```

## Expected Output
After executing these commands, you should see performance testing results, including model launch speed and additional metrics:


## Notes
Even if you plan to save the model in formats such as fp32, bf16, or best, the final model name in the TRT format must match the names specified in the commands.
