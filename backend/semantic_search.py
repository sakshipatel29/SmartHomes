# from elasticsearch import Elasticsearch
# from openai import OpenAI
# import json

# # Initialize Elasticsearch client
# es = Elasticsearch(['http://localhost:9200'])

# # Initialize OpenAI client with API key
# client = OpenAI(api_key="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")

# # Define model and embedding function
# EMBEDDING_MODEL = "text-embedding-3-small"

# def get_embedding(text, model="text-embedding-3-small"):
#     text = text.replace("\n", " ")
#     return client.embeddings.create(input=[text], model=model).data[0].embedding

# def semantic_search(query, index_name="products", top_k=10):
#     # Get the embedding for the query
#     query_embedding = get_embedding(query, model=EMBEDDING_MODEL)

#     # Perform the semantic search
#     response = es.search(
#         index=index_name,
#         body={
#             "size": top_k,
#             "query": {
#                 "script_score": {
#                     "query": {"match_all": {}},
#                     "script": {
#                         "source": "cosineSimilarity(params.query_vector, 'embedding_vector') + 1.0",
#                         "params": {"query_vector": query_embedding}
#                     }
#                 }
#             }
#         }
#     )

#     # Extract and return the relevant information
#     results = []
#     for hit in response['hits']['hits']:
#         result = {
#             "id": hit['_source']['id'],
#             "name": hit['_source']['name'],
#             "description": hit['_source']['description'],
#             "score": hit['_score']
#         }
#         results.append(result)

#     return results

# def main():
#     # Get user input
#     user_query = input("Enter your search query: ")

#     # Perform semantic search
#     search_results = semantic_search(user_query)

#     # Display results
#     print("\nSemantically similar product reviews:")
#     for result in search_results:
#         print(f"\nProduct ID: {result['id']}")
#         print(f"Name: {result['name']}")
#         print(f"Description: {result['description']}")
#         print(f"Similarity Score: {result['score']}")

# if __name__ == "__main__":
#     main()

from elasticsearch import Elasticsearch
import openai
from tqdm import tqdm

# Elasticsearch connection
es = Elasticsearch(['http://localhost:9200'])

# OpenAI API key
openai.api_key = "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"

def get_embedding(text, model="text-embedding-3-small"):
    """Get embedding for the input text using OpenAI API"""
    text = text.replace("\n", " ")
    response = openai.Embedding.create(input=[text], model=model)
    return response['data'][0]['embedding']

def search_similar_reviews(query_text, top_k=5):
    """Search for reviews semantically similar to the input query"""
    # Get embedding for the query text
    query_vector = get_embedding(query_text)

    # Elasticsearch query
    search_query = {
        "size": top_k,
        "query": {
            "script_score": {
                "query": {"match_all": {}},
                "script": {
                    "source": "cosineSimilarity(params.query_vector, 'embedding_vector') + 1.0",
                    "params": {"query_vector": query_vector}
                }
            }
        },
        "_source": ["productModelName", "reviewText", "reviewRating"]
    }

    # Execute the search
    response = es.search(index="products", body=search_query)

    # Process and return results
    results = []
    for hit in response['hits']['hits']:
        results.append({
            "product": hit['_source']['productModelName'],
            "review": hit['_source']['reviewText'],
            "rating": hit['_source']['reviewRating'],
            "score": hit['_score']
        })

    return results

# User interface
while True:
    user_input = input("Enter your search query (or 'quit' to exit): ")
    if user_input.lower() == 'quit':
        break

    similar_reviews = search_similar_reviews(user_input)
    
    print("\nSimilar reviews:")
    for i, review in enumerate(similar_reviews, 1):
        print(f"\n{i}. Product: {review['product']}")
        print(f"   Rating: {review['rating']}")
        print(f"   Review: {review['review'][:200]}...")  # Truncate long reviews
        print(f"   Similarity Score: {review['score']:.4f}")

    print("\n" + "-"*50)