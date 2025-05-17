#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>

#define PORT 8080
#define BUFFER_SIZE 1024
#define DB_FILE "books.txt"

typedef struct {
    char title[100];
    char author[100];
    int accession;
    int pages;
    char publisher[100];
} Book;

void insert_book(Book book);
int delete_book(int accession);
void display_books(int client_sock);
void search_books(char *query, int search_by, int client_sock);
void send_message(int client_sock, char *message);

int main() {
    int server_fd, new_socket;
    struct sockaddr_in address;
    int addrlen = sizeof(address);
    char buffer[BUFFER_SIZE];

    // Create server socket
    if ((server_fd = socket(AF_INET, SOCK_STREAM, 0)) == 0) {
        perror("Socket creation failed");
        exit(EXIT_FAILURE);
    }

    address.sin_family = AF_INET;
    address.sin_addr.s_addr = INADDR_ANY;
    address.sin_port = htons(PORT);

    if (bind(server_fd, (struct sockaddr *)&address, sizeof(address)) < 0) {
        perror("Bind failed");
        exit(EXIT_FAILURE);
    }

    if (listen(server_fd, 3) < 0) {
        perror("Listen failed");
        exit(EXIT_FAILURE);
    }

    printf("Server listening on port %d...\n", PORT);

    while (1) {
        if ((new_socket = accept(server_fd, (struct sockaddr *)&address, (socklen_t *)&addrlen)) < 0) {
            perror("Accept failed");
            exit(EXIT_FAILURE);
        }

        printf("Client connected.\n");

        while (1) {
            memset(buffer, 0, BUFFER_SIZE);
            if (recv(new_socket, buffer, BUFFER_SIZE, 0) <= 0) {
                printf("Client disconnected.\n");
                close(new_socket);
                break;
            }

            int option;
            sscanf(buffer, "%d", &option);

            if (option == 1) { // Insert book
                Book book;
                sscanf(buffer, "%d|%99[^|]|%99[^|]|%d|%d|%99[^\n]", &option, book.title, book.author, &book.accession, &book.pages, book.publisher);
                insert_book(book);
                send_message(new_socket, "Book inserted successfully.");
            } 
            else if (option == 2) { // Delete book
                int accession;
                sscanf(buffer, "%d|%d", &option, &accession);
                if (delete_book(accession))
                    send_message(new_socket, "Book deleted successfully.");
                else
                    send_message(new_socket, "Book not found.");
            } 
            else if (option == 3) { // Display all books
                display_books(new_socket);
            } 
            else if (option == 4) { // Search book
                int search_by;
                char query[100];
                sscanf(buffer, "%d|%d|%99[^\n]", &option, &search_by, query);
                search_books(query, search_by, new_socket);
            } 
            else if (option == 5) { // Exit
                send_message(new_socket, "Exit");
                break;
            }
        }
    }

    close(server_fd);
    return 0;
}

// Insert book into file
void insert_book(Book book) {
    FILE *file = fopen(DB_FILE, "a");
    if (file == NULL) return;
    fprintf(file, "%s|%s|%d|%d|%s\n", book.title, book.author, book.accession, book.pages, book.publisher);
    fclose(file);
}

// Delete book from file
int delete_book(int accession) {
    FILE *file = fopen(DB_FILE, "r");
    FILE *temp = fopen("temp.txt", "w");
    if (!file || !temp) return 0;

    Book book;
    int found = 0;
    while (fscanf(file, "%99[^|]|%99[^|]|%d|%d|%99[^\n]\n", book.title, book.author, &book.accession, &book.pages, book.publisher) != EOF) {
        if (book.accession == accession) {
            found = 1;
            continue;
        }
        fprintf(temp, "%s|%s|%d|%d|%s\n", book.title, book.author, book.accession, book.pages, book.publisher);
    }

    fclose(file);
    fclose(temp);
    remove(DB_FILE);
    rename("temp.txt", DB_FILE);
    return found;
}

// Display all books
void display_books(int client_sock) {
    FILE *file = fopen(DB_FILE, "r");
    char buffer[BUFFER_SIZE] = "";
    if (!file) {
        send_message(client_sock, "No books available.");
        return;
    }

    Book book;
    while (fscanf(file, "%99[^|]|%99[^|]|%d|%d|%99[^\n]\n", book.title, book.author, &book.accession, &book.pages, book.publisher) != EOF) {
        char temp[256];
        snprintf(temp, sizeof(temp), "Title: %s, Author: %s, Accession: %d, Pages: %d, Publisher: %s\n", book.title, book.author, book.accession, book.pages, book.publisher);
        strcat(buffer, temp);
    }
    fclose(file);
    send_message(client_sock, buffer);
}

// Search book by title or author
void search_books(char *query, int search_by, int client_sock) {
    FILE *file = fopen(DB_FILE, "r");
    char buffer[BUFFER_SIZE] = "";
    if (!file) {
        send_message(client_sock, "No books found.");
        return;
    }

    Book book;
    int found = 0;
    while (fscanf(file, "%99[^|]|%99[^|]|%d|%d|%99[^\n]\n", book.title, book.author, &book.accession, &book.pages, book.publisher) != EOF) {
        if ((search_by == 1 && strcmp(book.title, query) == 0) || (search_by == 2 && strcmp(book.author, query) == 0)) {
            char temp[256];
            snprintf(temp, sizeof(temp), "Title: %s, Author: %s, Accession: %d, Pages: %d, Publisher: %s\n", book.title, book.author, book.accession, book.pages, book.publisher);
            strcat(buffer, temp);
            found = 1;
        }
    }
    fclose(file);
    if (found) send_message(client_sock, buffer);
    else send_message(client_sock, "Book not found.");
}

// Send message to client
void send_message(int client_sock, char *message) {
    send(client_sock, message, strlen(message), 0);
}
