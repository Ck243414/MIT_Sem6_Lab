#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>

#define PORT 8080
#define BUFFER_SIZE 1024

void send_message(int sock, char *message);

int main() {
    int sock;
    struct sockaddr_in server_addr;
    char buffer[BUFFER_SIZE];

    // Create client socket
    if ((sock = socket(AF_INET, SOCK_STREAM, 0)) < 0) {
        perror("Socket creation failed");
        exit(EXIT_FAILURE);
    }

    server_addr.sin_family = AF_INET;
    server_addr.sin_port = htons(PORT);
    server_addr.sin_addr.s_addr = inet_addr("127.0.0.1");

    if (connect(sock, (struct sockaddr *)&server_addr, sizeof(server_addr)) < 0) {
        perror("Connection failed");
        exit(EXIT_FAILURE);
    }

    printf("Connected to server.\n");

    while (1) {
        int choice;
        printf("\n📚 Book Database Menu 📚\n");
        printf("1. Insert Book\n");
        printf("2. Delete Book\n");
        printf("3. Display All Books\n");
        printf("4. Search Book\n");
        printf("5. Exit\n");
        printf("Enter your choice: ");
        scanf("%d", &choice);
        getchar(); // Consume newline

        memset(buffer, 0, BUFFER_SIZE);
        if (choice == 1) { // Insert book
            char title[100], author[100], publisher[100];
            int accession, pages;
            printf("Enter Title: ");
            fgets(title, 100, stdin);
            printf("Enter Author: ");
            fgets(author, 100, stdin);
            printf("Enter Accession Number: ");
            scanf("%d", &accession);
            printf("Enter Total Pages: ");
            scanf("%d", &pages);
            getchar(); // Consume newline
            printf("Enter Publisher: ");
            fgets(publisher, 100, stdin);

            snprintf(buffer, BUFFER_SIZE, "1|%s|%s|%d|%d|%s", title, author, accession, pages, publisher);
        } 
        else if (choice == 2) { // Delete book
            int accession;
            printf("Enter Accession Number to Delete: ");
            scanf("%d", &accession);
            snprintf(buffer, BUFFER_SIZE, "2|%d", accession);
        } 
        else if (choice == 3) { // Display books
            snprintf(buffer, BUFFER_SIZE, "3");
        } 
        else if (choice == 4) { // Search book
            int search_type;
            char query[100];
            printf("Search by:\n1. Title\n2. Author\nEnter choice: ");
            scanf("%d", &search_type);
            getchar(); // Consume newline
            printf("Enter search term: ");
            fgets(query, 100, stdin);
            snprintf(buffer, BUFFER_SIZE, "4|%d|%s", search_type, query);
        } 
        else if (choice == 5) { // Exit
            snprintf(buffer, BUFFER_SIZE, "5");
            send_message(sock, buffer);
            printf("Exiting...\n");
            break;
        } 
        else {
            printf("Invalid choice. Try again.\n");
            continue;
        }

        send_message(sock, buffer);

        // Receive server response
        memset(buffer, 0, BUFFER_SIZE);
        recv(sock, buffer, BUFFER_SIZE, 0);
        printf("\nServer: %s\n", buffer);
    }

    close(sock);
    return 0;
}

// Function to send a message to the server
void send_message(int sock, char *message) {
    send(sock, message, strlen(message), 0);
}
