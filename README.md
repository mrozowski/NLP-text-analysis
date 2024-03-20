# NLP text analysis demo

The project uses Apache Open NLP library for simple text analysis (Sentiment and NER). 

I used models from Apache OpenNLP that can be found [here](https://opennlp.apache.org/models.html) and [here](https://opennlp.sourceforge.net/models-1.5/).
However, I could not find a model for analyzing text sentiment so I wrote code to train my model. I prepared CSV file with labeled data with the help of chatGPT. You can find it in the [dataset](/dataset) directory.

### Technology stack
* Kotlin
* Spring boot
* Apache OpenNLP
* Apache Common CSV
* Junit5

## API
App has 2 endpoints

### 1. Text analysis

``` bash
curl -X GET -H "Content-Type: application/json" -d '{ "text": "I am working at Apple company with my friend Jeff"}' localhost:8080/v1/analyze
```

The endpoint takes JSON as a request with a single field `text` with the text we want to analyze. It returns JSON with sentiment, person names, and organization names

**Example response**
``` json
{
    "sentiment": "NEUTRAL",
    "namedEntity": {
        "personNames": [
            "Jeff"
        ],
        "organizationNames": [
            "Apple"
        ]
    }
}
```

### 2. Sentiment model training

``` bash
curl -X POST 'localhost:8080/v1/model/train-sentiment-model' -F 'file=@"dataset/labeled-sentiment-dataset.csv"' 
```
The endpoint takes a CSV file with text and sentiment columns. After successful upload, the app trains the model and saves it as `.bin` file in a `model/sentiment/` directory that can be changed in the application.yaml. 

To use a new model you have to update the application.yaml file with a path to a new sentimental model and restart the app. Later I might add an endpoint to change model without restarting app. It is just a simple demo app to see how OpenNLP library works

**Example response**
``` text
model/sentiment/en-sentiment-model-2024-03-20_22-06-33.bin
```


The sentiment model has around 84% accuracy. With more inputs, it could analyze text sentiment with better accuracy. Here are some example responses:

| Input                                                 | Sentiment model response | Result  |
|-------------------------------------------------------|-------------------------|---------|
| "My car broke and I can't get to the job"             | "NEGATIVE"              | ✅      |
| "This news is very sad"                               | "NEGATIVE"              | ✅      |
| "Their products are affordable and have good quality" | "NEGATIVE"              | ❌      |
| "The view from the top of the mountain was awesome"   | "POSITIVE"              | ✅      |
| "Another day, another dollar"                         | "NEUTRAL"               | ✅      |
| "This new game is really amazing"                     | "POSITIVE"              | ✅      |
| "I feel sad"                                          | "NEUTRAL"               | ❌      |

