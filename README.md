# MAP_ToySocialNetwork

A console-based toy social network written in Java, built for the *Advanced Programming Methods* course .

## Features

- Add and remove users
- Add and remove friendships (friendships are symmetric: Ana–Dan is the same as Dan–Ana)
- Removing a user also removes all of their friendships
- Count the number of communities (connected components of the friendship graph)
- Find the most sociable community (the connected component containing the longest simple path)

## Architecture

The project follows a layered architecture:

| Layer | Package | Responsibility |
|---|---|---|
| UI | `ui` | Console menu, reads input and prints results |
| Service | `service` | Business logic and graph algorithms |
| Repository | `repository` | Generic in-memory storage |
| Domain | `domain` | Entities, validators and custom exceptions |

Design notes:

- **Generic repository** (`Repository<ID, E extends Entity<ID>>`) with an in-memory implementation.
- **Strategy pattern** for validation: a `Validator<T>` is injected into each repository.
- **Custom exceptions** (`ValidationException`, `ServiceException`) for domain and business rule errors.
- A friendship is identified by a normalized pair of user ids, so the order of the two users does not matter.

## Algorithms

- **Number of communities:** build an adjacency list, then run DFS from every unvisited user and count the starts.
- **Most sociable community:** for every community, run a DFS with backtracking from each member to find the longest simple path, then keep the community with the longest one.

## Getting started

Requirements: JDK 21 or newer.

1. Clone the repository:
```
   git clone https://github.com/demidovcezar/MAP_ToySocialNetwork.git
```
2. Open the project in IntelliJ IDEA (mark `src` as *Sources Root*).
3. Run `Main`.

## Console menu

```
1. Add user
2. Remove user
3. Add friendship
4. Remove friendship
5. Show users
6. Show friendships
7. Number of communities
8. Most sociable community
0. Exit
```

## Documentation

The generated Javadoc is in the `documentation` folder.
