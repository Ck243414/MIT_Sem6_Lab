// server.c
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <ctype.h>

#define PORT 8080
#define MAX_BUF 1024

// Function to compare two characters for descending order
int compare_desc(const void *a, const void *b) {
    return (*(char *)b - *(char *)a);
}

// Function to compare two integers for ascending order
int compare_asc(const void *a, const void *b) {
    return (*(int *)a - *(int *)b);
}

// Function to sort and send numbers
void handle_numbers(char *str, int sockfd, struct sockaddr_in *client_addr, socklen_t addr_len) {
    char numbers[MAX_BUF];
    int num_count = 0;
    
    // Extract numbers from the string
    for (int i = 0; str[i] != '\0'; i++) {
        if (isdigit(str[i])) {
            numbers[num_count++] = str[i];
        }
    }
    numbers[num_count] = '\0';
    
    // Sort the numbers in ascending order
    qsort(numbers, num_count, sizeof(char), compare_asc);
    
    // Send sorted numbers back to client
    sendto(sockfd, numbers, strlen(numbers), 0, (struct sockaddr *)client_addr, addr_len);
    
    // Send the process ID (PID) of the child process
    char pid_msg[MAX_BUF];
    sprintf(pid_msg, "Child Process PID: %d", getpid());
    sendto(sockfd, pid_msg, strlen(pid_msg), 0, (struct sockaddr *)client_addr, addr_len);
}

// Function to sort and send characters
void handle_characters(char *str, int sockfd, struct sockaddr_in *client_addr, socklen_t addr_len) {
    char characters[MAX_BUF];
    int char_count = 0;
    
    // Extract characters from the string
    for (int i = 0; str[i] != '\0'; i++) {
        if (isalpha(str[i])) {
            characters[char_count++] = str[i];
        }
    }
    characters[char_count] = '\0';
    
    // Sort the characters in descending order
    qsort(characters, char_count, sizeof(char), compare_desc);
    
    // Send sorted characters back to client
    sendto(sockfd, characters, strlen(characters), 0, (struct sockaddr *)client_addr, addr_len);
    
    // Send the process ID (PID) of the parent process
    char pid_msg[MAX_BUF];
    sprintf(pid_msg, "Parent Process PID: %d", getpid());
    sendto(sockfd, pid_msg, strlen(pid_msg), 0, (struct sockaddr *)client_addr, addr_len);
}

int main() {
    int server_fd, new_socket;
    struct sockaddr_in server_addr, client_addr;
    socklen_t addr_len = sizeof(client_addr);
    char buffer[MAX_BUF];
    
    // Create socket
    if ((server_fd = socket(AF_INET, SOCK_STREAM, 0)) == 0) {
        perror("Socket failed");
        exit(EXIT_FAILURE);
    }

    server_addr.sin_family = AF_INET;
    server_addr.sin_addr.s_addr = INADDR_ANY;
    server_addr.sin_port = htons(PORT);

    // Bind the socket to the port
    if (bind(server_fd, (struct sockaddr *)&server_addr, sizeof(server_addr)) < 0) {
        perror("Bind failed");
        exit(EXIT_FAILURE);
    }

    // Listen for incoming connections
    if (listen(server_fd, 3) < 0) {
        perror("Listen failed");
        exit(EXIT_FAILURE);
    }

    printf("Server is listening on port %d...\n", PORT);

    // Accept incoming connection
    if ((new_socket = accept(server_fd, (struct sockaddr *)&client_addr, &addr_len)) < 0) {
        perror("Accept failed");
        exit(EXIT_FAILURE);
    }

    // Receive the alphanumeric string from the client
    int n = recv(new_socket, buffer, sizeof(buffer), 0);
    buffer[n] = '\0';
    printf("Received string from client: %s\n", buffer);

    // Fork to create a child process for number sorting
    pid_t pid = fork();

    if (pid == 0) {
        // Child process: handle number sorting
        handle_numbers(buffer, new_socket, &client_addr, addr_len);
        close(new_socket);
        exit(0);
    } else if (pid > 0) {
        // Parent process: handle character sorting
        handle_characters(buffer, new_socket, &client_addr, addr_len);
        close(new_socket);
    } else {
        perror("Fork failed");
        exit(EXIT_FAILURE);
    }

    // Close the server socket
    close(server_fd);
    return 0;
}
