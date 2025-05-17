#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <time.h>

#define PORT 8080
#define MAX_BUF 1024

// Function to check if two strings are anagrams
int are_anagrams(char *str1, char *str2) {
    int count[256] = {0};

    // If lengths of the two strings are different, they can't be anagrams
    if (strlen(str1) != strlen(str2)) {
        return 0;
    }

    // Count frequency of each character in both strings
    for (int i = 0; str1[i] != '\0'; i++) {
        count[str1[i]]++;
        count[str2[i]]--;
    }

    // If all frequencies are zero, they are anagrams
    for (int i = 0; i < 256; i++) {
        if (count[i] != 0) {
            return 0;
        }
    }
    return 1;
}

int main() {
    int server_socket, client_socket;
    struct sockaddr_in server_addr, client_addr;
    socklen_t addr_len = sizeof(client_addr);
    char buffer[MAX_BUF], result[MAX_BUF];
    time_t t;
    struct tm *tm_info;

    // Create server socket
    if ((server_socket = socket(AF_INET, SOCK_STREAM, 0)) == -1) {
        perror("Socket creation failed");
        exit(EXIT_FAILURE);
    }

    server_addr.sin_family = AF_INET;
    server_addr.sin_addr.s_addr = INADDR_ANY;
    server_addr.sin_port = htons(PORT);

    // Bind socket to the address and port
    if (bind(server_socket, (struct sockaddr *)&server_addr, sizeof(server_addr)) == -1) {
        perror("Bind failed");
        close(server_socket);
        exit(EXIT_FAILURE);
    }

    // Listen for incoming connections
    if (listen(server_socket, 5) == -1) {
        perror("Listen failed");
        close(server_socket);
        exit(EXIT_FAILURE);
    }

    printf("Server is listening on port %d...\n", PORT);

    while (1) {
        // Accept client connection
        client_socket = accept(server_socket, (struct sockaddr *)&client_addr, &addr_len);
        if (client_socket == -1) {
            perror("Accept failed");
            continue;
        }

        // Get current date and time
        time(&t);
        tm_info = localtime(&t);
        char date_time[50];
        strftime(date_time, 50, "%Y-%m-%d %H:%M:%S", tm_info);

        // Display connection info
        printf("Connected to client %s:%d at %s\n", inet_ntoa(client_addr.sin_addr), ntohs(client_addr.sin_port), date_time);

        // Receive two strings from the client
        recv(client_socket, buffer, MAX_BUF, 0);
        char *str1 = strtok(buffer, " ");
        char *str2 = strtok(NULL, " ");

        if (str1 != NULL && str2 != NULL) {
            // Check if the strings are anagrams
            if (are_anagrams(str1, str2)) {
                strcpy(result, "The strings are anagrams.");
            } else {
                strcpy(result, "The strings are not anagrams.");
            }
        } else {
            strcpy(result, "Error: Invalid input from client.");
        }

        // Send result back to the client
        send(client_socket, result, strlen(result), 0);

        // Close client socket
        close(client_socket);
    }

    // Close server socket (never reached in this code)
    close(server_socket);
    return 0;
}
