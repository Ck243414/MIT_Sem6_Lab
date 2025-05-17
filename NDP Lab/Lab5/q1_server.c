#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <pthread.h>

#define PORT 8080
#define MAX_CLIENTS 2

// Global variables to track the clients
int client_count = 0;
pthread_mutex_t client_count_mutex = PTHREAD_MUTEX_INITIALIZER;

// Structure to hold client information
typedef struct {
    int socket;
    struct sockaddr_in address;
} client_info;

// Function to handle communication with each client
void* handle_client(void* arg) {
    client_info client = *((client_info*)arg);
    char buffer[1024];
    int bytes_read;

    // Receive the message from the client
    bytes_read = recv(client.socket, buffer, sizeof(buffer), 0);
    if (bytes_read <= 0) {
        perror("Error receiving data");
        close(client.socket);
        return NULL;
    }

    buffer[bytes_read] = '\0'; // Null-terminate the string
    printf("Received message: '%s' from %s:%d\n", buffer, inet_ntoa(client.address.sin_addr), ntohs(client.address.sin_port));

    // Open the file and append the received data
    FILE *file = fopen("manipal.txt", "a+");
    if (!file) {
        perror("Error opening file");
        close(client.socket);
        return NULL;
    }

    // Write to the file
    fprintf(file, "%s ", buffer);
    fclose(file);

    // Send acknowledgment back to the client
    char response[] = "Data received successfully!";
    send(client.socket, response, sizeof(response), 0);

    // Check if the number of clients exceeds 2
    pthread_mutex_lock(&client_count_mutex);
    client_count++;
    if (client_count > MAX_CLIENTS) {
        char terminate_message[] = "Terminate session";
        send(client.socket, terminate_message, sizeof(terminate_message), 0);
        close(client.socket);
        pthread_mutex_unlock(&client_count_mutex);
        return NULL;
    }
    pthread_mutex_unlock(&client_count_mutex);

    // Close the client socket
    close(client.socket);
    return NULL;
}

int main() {
    int server_socket, new_socket;
    struct sockaddr_in server_addr, client_addr;
    socklen_t addr_len = sizeof(client_addr);
    pthread_t thread_id;

    // Create server socket
    if ((server_socket = socket(AF_INET, SOCK_STREAM, 0)) == 0) {
        perror("Socket failed");
        exit(EXIT_FAILURE);
    }

    // Set server address and bind
    server_addr.sin_family = AF_INET;
    server_addr.sin_addr.s_addr = INADDR_ANY;
    server_addr.sin_port = htons(PORT);

    if (bind(server_socket, (struct sockaddr*)&server_addr, sizeof(server_addr)) < 0) {
        perror("Bind failed");
        exit(EXIT_FAILURE);
    }

    // Listen for incoming connections
    if (listen(server_socket, 5) < 0) {
        perror("Listen failed");
        exit(EXIT_FAILURE);
    }

    printf("Server is running on port %d...\n", PORT);

    while (1) {
        // Accept client connections
        new_socket = accept(server_socket, (struct sockaddr*)&client_addr, &addr_len);
        if (new_socket < 0) {
            perror("Accept failed");
            continue;
        }

        client_info* c_info = malloc(sizeof(client_info));
        c_info->socket = new_socket;
        c_info->address = client_addr;

        // Create a new thread to handle the client
        if (pthread_create(&thread_id, NULL, handle_client, (void*)c_info) != 0) {
            perror("Thread creation failed");
            close(new_socket);
        }

        pthread_detach(thread_id);
    }

    return 0;
}

