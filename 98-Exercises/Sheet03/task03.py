import torch
from sentence_transformers import SentenceTransformer, util
from transformers import AutoTokenizer, AutoModelForSequenceClassification
import json

def json_import():
    try:
        with open('PEx_2_TextRetrieval/data/movie_dataset.jsonl', 'r') as json_file:
            json_list = list(json_file)
        
        results = []
        for json_str in json_list:
            results.append(json.loads(json_str)) # a list of dict
        return results
    
    except FileNotFoundError:
        print("Error: The file 'data.json' was not found.")


def extract_texts_from_entries(results):
    movie_texts = []
    for result in results:
        text = ""
        text = text + (result["title"]) + ", "
        text = text + (result["overview"]) + ", "
        text = text + (result["cast"]) + ", "
        text = text + (result["tagline"])
        movie_texts.append(text)
    return movie_texts
        



def embeddings_on_text(movie_texts):
    bi_encoder = SentenceTransformer("Qwen/Qwen3-Embedding-0.6B")
    texts = []
    for i in range(1000): # first run was with 100 now 1000 wont probably go on full size of 3001
        texts.append(movie_texts[i])

    embeddings = bi_encoder.encode(texts, show_progress_bar=True)
    torch.save(embeddings, "./embeddings.pt")

def queries_from_embeddings(queries):
    # Batch multiple queries
    bi_encoder = SentenceTransformer("Qwen/Qwen3-Embedding-0.6B")
    q_embeddings = bi_encoder.encode(queries, prompt_name="query")
    hits = util.semantic_search(q_embeddings, embeddings, top_k=10)
    # Print results for the queries
    # print(hits)
    return(hits)
def print_results_from_hits(hits, score_param=0):
    counter = 0
    for hit in hits:
        counter += 1
        print("Query", counter)
        print("---------------------------- \n")
        if score_param == 1:
            # TODO print firts only
            print("----------------------------")
            print("First Movie name from results")
            id = hit[0]["corpus_id"]
            print(results[id]["title"], hit[0]["score"])
            print("----------------------------")
        for entry in hit:
            # print("id's of hits for query: ")
            id = entry["corpus_id"]
            # print(id)
            if entry["score"] > score_param:
                print("----------------------------")
                print("Movie name from results with score greater than: ", score_param)
                print(results[id]["title"], entry["score"])
                print("----------------------------")

def test_different_scores_for_hits():
    # test result retrieval with scores
    print("Print all hits for any scores")
    print_results_from_hits(hits)
    print("-_-_-_-_-_-_-_-_-_-_-_-_")
    print("Print scores for 0.5 or greater")
    print_results_from_hits(hits, score_param=0.5)
    print("-_-_-_-_-_-_-_-_-_-_-_-_")
    print("Print first hit of query")
    print_results_from_hits(hits, score_param=1)

'''
def reranker():
    # TODO: implement cross encoder
'''

# done with chatgpt:
def reranker(query, hits_for_query, movie_texts, top_k=10):

    tokenizer = AutoTokenizer.from_pretrained(
        "Qwen/Qwen3-Reranker-0.6B",
        trust_remote_code=True
    )
    model = AutoModelForSequenceClassification.from_pretrained(
        "Qwen/Qwen3-Reranker-0.6B",
        trust_remote_code=True
    )
    model.eval()

    tokenizer.pad_token = tokenizer.eos_token
    model.config.pad_token_id = tokenizer.eos_token_id

    candidates = hits_for_query[:top_k]

    pairs = [
        (query, movie_texts[hit["corpus_id"]])
        for hit in candidates
    ]

    inputs = tokenizer(
        pairs,
        padding=True,
        truncation=True,
        return_tensors="pt"
    )

    with torch.no_grad():
        scores = model(**inputs).logits.squeeze(-1)

    reranked_results = sorted(
        zip(candidates, scores.tolist()),
        key=lambda x: x[1],
        reverse=True
    )

    return reranked_results



if __name__ == '__main__':
    results = json_import()
    movie_texts = extract_texts_from_entries(results)
    # embeddings_on_text(movie_texts)
    
    embeddings = torch.load("./embeddings.pt", weights_only=False)
    queries = ["toy story", "Seth Gecko and his younger brother Richard are on the run after a bloody bank robbery in Texas. They escape across the border into Mexico and will be home-free the next morning, when they pay off the local kingpin. They just have to survive 'from dusk till dawn' at the rendezvous point, which turns out to be a Hell of a strip joint.", "From dusk till dawn", "dune" ]
    hits = queries_from_embeddings(queries)
    # test_different_scores_for_hits()
    # with chatgpt
    reranked_results = reranker(queries[0], hits[0], movie_texts, top_k=10)
    for hit, score in reranked_results[:10]:
        print(results[hit["corpus_id"]]["title"], score)