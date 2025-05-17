#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>

#define PORT 8080
#define BUFFER_SIZE 1024
#define DATABASE_FILE "database.txt"

void lookup_dns(const char *domain, char *result) {
    FILE *file = fopen(DATABASE_FILE, "r");
    if (!file) {
        strcpy(result, "Database file not found.");
        return;
    }

    char line[BUFFER_SIZE], stored_domain[BUFFER_SIZE], stored_ip[BUFFER_SIZE];
    while (fgets(line, sizeof(line), file)) {
        sscanf(line, "%s %s", stored_domain, stored_ip);
        if (strcmp(domain, stored_domain) == 0) {
            sprintf(result, "IP Address: %s", stored_ip);
            fclose(file);
            return;
        }
    }
    
    strcpy(result, "Domain not found in database.");
    fclose(file);
}

int main() {
    int server_fd, client_sock;
    struct sockaddr_in server_addr, client_addr;
    socklen_t addr_len = sizeof(client_addr);
    char buffer[BUFFER_SIZE], response[BUFFER_SIZE];

    server_fd = socket(AF_INET, SOCK_STREAM, 0);
    if (server_fd == -1) {
        perror("Socket creation failed");
        exit(EXIT_FAILURE);
    }

    server_addr.sin_family = AF_INET;
    server_addr.sin_addr.s_addr = INADDR_ANY;
    server_addr.sin_port = htons(PORT);

    if (bind(server_fd, (struct sockaddr *)&server_addr, sizeof(server_addr)) == -1) {
        perror("Binding failed");
        close(server_fd);
        exit(EXIT_FAILURE);
    }

    if (listen(server_fd, 5) == -1) {
        perror("Listening failed");
        close(server_fd);
        exit(EXIT_FAILURE);
    }

    printf("DNS Server listening on port %d...\n", PORT);

    while (1) {
        client_sock = accept(server_fd, (struct sockaddr *)&client_addr, &addr_len);
        if (client_sock == -1) {
            perror("Accept failed");
            continue;
        }

        read(client_sock, buffer, BUFFER_SIZE);
        printf("Received request for domain: %s\n", buffer);

        lookup_dns(buffer, response);
        send(client_sock, response, strlen(response), 0);

        close(client_sock);
    }

    close(server_fd);
    return 0;
}
