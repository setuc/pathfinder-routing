# Routing Master

We tried several approaches to optimize routes:
- Linear Programming
- A\* Heuristics
- Simulated Annealing

In the end, each approach has it's pros and its cons. And since Google Compute
Engine is fairly cheap, we run all of them simultaneously. This server is
responsible for selecting the best of the best routes.

Install dependencies with `bundle install`. Run with `rackup`.
