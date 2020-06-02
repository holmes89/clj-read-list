# Reading List

I needed a way to keep track of books that I wish to read or have read. This is an application that was used to teach myself Clojure and some other technologies therefore this is a work in progress. Application will take an ISBN number and call [Google Books API](https://books.google.com) and add it to the application. The user can then mark the book as read or liked.

## Future Features

- [ ] Deletion
- [ ] User defined lists
- [ ] Authentication

## Requirements

- Redis Database

## Featured Technologies

- Clojure
- ClojureScript
 - Reframe
- Redis
- Heroku

## Running

- `export REDIS_URL=redis://url-to-redis`
- `lein dev` for local development
- `lein prod` for production
