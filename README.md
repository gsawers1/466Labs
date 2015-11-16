# 466Labs

Various Knowledge Discovery and Data Mining algorithms implemented (primarily) in Java.

Information Retrevial: A querying program designed to run on a collection of documents stored in a JSON Array, then use the Vector Space Model to organize these documents and retrieve them based on given queries.  Uses stemming and stopword removal to better categorize documents. Then determines keyword weights inside of the documents to calculate Cosine Similarity or Okapi Score (depending what the user has chosen) based on a given query.

PageRank: A basic implementation of Sergey Brin and Larry Page's PageRank algorithm developed at Stanford, described in their paper "The Anatomy of a Large-Scale Hypertextual Web Search Engine". http://infolab.stanford.edu/~backrub/google.html

Our algorithm is designed to run either directed or undirected graphs, represented by the two Node classes. One is a generic Node for general csv parsing, while the other is specialized, designed to work with SNAP datasets from the Stanford Network Analysis Project. http://snap.stanford.edu/ 


