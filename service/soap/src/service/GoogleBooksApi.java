package service;

import model.Book;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.*;

public class GoogleBooksApi {
    private static final String API_KEY = "AIzaSyCaJ4-5uWO1ntq_RZ2TpTuAkzJuAPiOlT4";
    private static final String STRING_UNDEFINED = "Tidak_Diketahui";

    /*
      input: string judul
      output: array of book
      attribute dari book:
      'id'
      'title'
      'author'
      'description'
      'image'
      'category'
     */
    public static ArrayList<Book> getBookByTitle(String title) {
        String apiUrlString = "https://www.googleapis.com/books/v1/volumes?q=intitle:" + title + "&key=" + API_KEY + "&maxResults=40";
        return getBooks(apiUrlString);
    }

    public static ArrayList<Book> getBookByCategory(String category) {
        String apiUrlString = "https://www.googleapis.com/books/v1/volumes?q=subject:" + category + "&key=" + API_KEY + "&maxResults=40";
        return getBooks(apiUrlString);
    }

    private static ArrayList<Book> getBooks(String apiUrlString) {
        try {
            ArrayList<Book> Books = new ArrayList<>();
            HttpURLConnection connection = null;
            // Build Connection.
            connection = getHttpURLConnection(apiUrlString, connection);
            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                connection.disconnect();
                return null;
            }

            // Read data from response.
            StringBuilder builder = new StringBuilder();
            BufferedReader responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = responseReader.readLine();
            while (line != null) {
                builder.append(line);
                line = responseReader.readLine();
            }
            String responseString = builder.toString();
            System.out.println("Response String: " + responseString);
            JSONObject responseJson = new JSONObject(responseString);
            // Close connection and return response code.
            connection.disconnect();

            if (responseJson.has("items")) {
                JSONArray arrayOfBooks = responseJson.getJSONArray("items");
                for (int i = 0; i < arrayOfBooks.length(); ++i) {
                    Book entry = getBook(arrayOfBooks.getJSONObject(i));
                    if (entry.getImage() != STRING_UNDEFINED && entry.getCategory() != STRING_UNDEFINED){
                        Books.add(entry);
                    }
                }
            } else {
                System.out.println("Hasil kosong");
            }

            return Books;
        } catch (SocketTimeoutException e) {
            System.out.println("Connection timed out. Returning null");
            return null;
        } catch (IOException e) {
            System.out.println("IOException when connecting to Google Books API.");
            return null;
        } catch (JSONException e) {
            System.out.println("JSONException when connecting to Google Books API.");
            return null;
        }
    }

    public static Book getBookDetailByID(String idBuku) {
        String apiUrlString = "https://www.googleapis.com/books/v1/volumes/" + idBuku;
        try {
            HttpURLConnection connection = null;
            // Build Connection.
            connection = getHttpURLConnection(apiUrlString, connection);
            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                connection.disconnect();
                return null;
            }

            // Read data from response.
            StringBuilder builder = new StringBuilder();
            BufferedReader responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = responseReader.readLine();
            while (line != null) {
                builder.append(line);
                line = responseReader.readLine();
            }
            String responseString = builder.toString();
            System.out.println("Response String: " + responseString);
            JSONObject responseJson = new JSONObject(responseString);
            // Close connection and return response code.
            connection.disconnect();

            return getBook(responseJson);
        } catch (SocketTimeoutException e) {
            System.out.println("Connection timed out. Returning null");
            return null;
        } catch (IOException e) {
            System.out.println("IOException when connecting to Google Books API.");
            return null;
        } catch (JSONException e) {
            System.out.println("JSONException when connecting to Google Books API.");
            return null;
        }
    }

    public static Book getRandomBookByCategory(String category) {
        String apiUrlString = "https://www.googleapis.com/books/v1/volumes?q=subject:" + category;
        try {
            HttpURLConnection connection = null;
            // Build Connection.
            connection = getHttpURLConnection(apiUrlString, connection);
            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                connection.disconnect();
                return null;
            }

            // Read data from response.
            StringBuilder builder = new StringBuilder();
            BufferedReader responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = responseReader.readLine();
            while (line != null) {
                builder.append(line);
                line = responseReader.readLine();
            }
            String responseString = builder.toString();
            System.out.println("Response String: " + responseString);
            JSONObject responseJson = new JSONObject(responseString);
            // Close connection and return response code.
            connection.disconnect();

            if (responseJson.has("items")) {
                JSONArray arrayOfBooks = responseJson.getJSONArray("items");
                Random rand = new Random();
                JSONObject jsonBook = arrayOfBooks.getJSONObject(rand.nextInt(arrayOfBooks.length()));
                return getBook(jsonBook);
            } else {
                System.out.println("Hasil kosong");
            }

            return null;
        } catch (SocketTimeoutException e) {
            System.out.println("Connection timed out. Returning null");
            return null;
        } catch (IOException e) {
            System.out.println("IOException when connecting to Google Books API.");
            return null;
        } catch (JSONException e) {
            System.out.println("JSONException when connecting to Google Books API.");
            return null;
        }
    }

    private static HttpURLConnection getHttpURLConnection(String apiUrlString, HttpURLConnection connection) throws IOException {
        try {
            URL url = new URL(apiUrlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(5000); // 5 seconds
            connection.setConnectTimeout(5000); // 5 seconds
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        return connection;
    }

    private static Book getBook(JSONObject responseJson) {
        Book entry = new Book();
        JSONObject cur = responseJson;

        String IDBuku;
        String JudulBuku;
        String AuthorBuku;
        String DeskripsiBuku;
        String ImageBuku;
        String KategoriBuku;

        if (cur.has("id")) {
            IDBuku = cur.getString("id");
        } else {
            IDBuku = STRING_UNDEFINED;
        }
        System.out.println("\nID: " + IDBuku);

        if (cur.has("volumeInfo")) {
            JSONObject volumeInfo = cur.getJSONObject("volumeInfo");

            if (volumeInfo.has("title")) {
                JudulBuku = volumeInfo.getString("title");
            } else {
                JudulBuku = STRING_UNDEFINED;
            }

            if (volumeInfo.has("categories")) {
                KategoriBuku = volumeInfo.getJSONArray("categories").getString(0);
            } else {
                KategoriBuku = STRING_UNDEFINED;
            }

            if (volumeInfo.has("authors")) {
                AuthorBuku = volumeInfo.getJSONArray("authors").getString(0);
            } else {
                AuthorBuku = STRING_UNDEFINED;
            }

            if (volumeInfo.has("description")) {
                DeskripsiBuku = volumeInfo.getString("description");
            } else {
                DeskripsiBuku = STRING_UNDEFINED;
            }

            if (volumeInfo.has("imageLinks")) {
                JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                if (imageLinks.has("thumbnail")) {
                    ImageBuku = imageLinks.getString("thumbnail");
                } else {
                    ImageBuku = STRING_UNDEFINED;
                }
            } else {
                ImageBuku = STRING_UNDEFINED;
            }

            System.out.println("Title: " + JudulBuku);
            System.out.println("Author: " + AuthorBuku);
            System.out.println("Kategori: " + KategoriBuku);
            System.out.println("Deskripsi: " + DeskripsiBuku);
            System.out.println("Image: " + ImageBuku);
        } else {
            System.out.println("Info buku dengan ID:" + IDBuku + "kosong");
            JudulBuku = STRING_UNDEFINED;
            KategoriBuku = STRING_UNDEFINED;
            AuthorBuku = STRING_UNDEFINED;
            DeskripsiBuku = STRING_UNDEFINED;
            ImageBuku = STRING_UNDEFINED;
        }

        entry.setId(IDBuku);
        entry.setTitle(JudulBuku);
        entry.setAuthor(AuthorBuku);
        entry.setDescription(DeskripsiBuku);
        entry.setImage(ImageBuku);
        entry.setCategory(KategoriBuku);
        return entry;
    }

}
