#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/wait.h>

#define PORT 8080
#define BUFFER_SIZE 1024

// Function to handle Registration Number request
void handle_registration(int client_sock) {
    char response[BUFFER_SIZE] = "Name: John Doe\nAddress: 123 University St, City, Country\n";
    sprintf(response + strlen(response), "Handled by Child Process PID: %d", getpid());
    send(client_sock, response, strlen(response), 0);
}

// Function to handle Name request
void handle_name(int client_sock) {
    char response[BUFFER_SIZE] = "Department: Computer Science\nSemester: 5\nSection: A\nCourses: DS, OS, Networks\n";
    sprintf(response + strlen(response), "Handled by Child Process PID: %d", getpid());
    send(client_sock, response, strlen(response), 0);
}

// Function to handle Subject Code request
void handle_subject(int client_sock) {
    char response[BUFFER_SIZE] = "Marks: 85/100\n";
    sprintf(response + strlen(response), "Handled by Child Process PID: %d", getpid());
    send(client_sock, response, strlen(response), 0);
}

int main() {
    int server_fd, client_sock;
    struct sockaddr_in serv_addr, cli_addr;
    socklen_t addr_len = sizeof(cli_addr);
    char buffer[BUFFER_SIZE];

    server_fd = socket(AF_INET, SOCK_STREAM, 0);
    if (server_fd == -1) {
        perror("Socket creation failed");
        exit(EXIT_FAILURE);
    }

    serv_addr.sin_family = AF_INET;
    serv_addr.sin_addr.s_addr = INADDR_ANY;
    serv_addr.sin_port = htons(PORT);

    if (bind(server_fd, (struct sockaddr *)&serv_addr, sizeof(serv_addr)) == -1) {
        perror("Binding failed");
        close(server_fd);
        exit(EXIT_FAILURE);
    }

    if (listen(server_fd, 5) == -1) {
        perror("Listening failed");
        close(server_fd);
        exit(EXIT_FAILURE);
    }

    printf("Server listening on port %d...\n", PORT);

    while (1) {
        client_sock = accept(server_fd, (struct sockaddr *)&cli_addr, &addr_len);
        if (client_sock == -1) {
            perror("Accept failed");
            continue;
        }

        if (fork() == 0) {
            close(server_fd);
            memset(buffer, 0, BUFFER_SIZE);
            read(client_sock, buffer, BUFFER_SIZE);
            printf("Received request: %s\n", buffer);

            int option;
            sscanf(buffer, "%d", &option);

            if (option == 1) {
                handle_registration(client_sock);
            } else if (option == 2) {
                handle_name(client_sock);
            } else if (option == 3) {
                handle_subject(client_sock);
            } else {
                send(client_sock, "Invalid Option", strlen("Invalid Option"), 0);
            }

            close(client_sock);
            exit(0);
        } else {
            close(client_sock);
            wait(NULL);
        }
    }

    close(server_fd);
    return 0;
}
