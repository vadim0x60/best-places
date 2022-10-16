# Best places on Google Maps

***Warning: you can spend a lot of money on Maps API*** .
The free tier lets you make a single digit number of `(best-places)` calls.
If you make more, Google will charge you, and it's easy to get into thousands of dollars.
For a cheaper solution check out [top-rated.online](https://www.top-rated.online/)

## Why

Google maps is good at almost everything.
One thing it's terrible at, however, is sorting your search results.
I found empirically that the best restaraunts/shops/anything are ones with a very high rating *and* 1000s of reviews.
Ratings based on a small number of reviews are unreliable.
So I prefer to sort them by

```
(stars - 4) * reviewCount
```

## How

Set `GOOGLE_API_KEY` environment variable to your Google Maps API key.
Call `(best-places query)`, i.e. `(best-places "restaurants in san francisco")`.