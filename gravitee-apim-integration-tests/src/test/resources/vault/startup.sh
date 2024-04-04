#!/bin/sh
#
# Copyright © 2015 The Gravitee team (http://gravitee.io)
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#


#####
# (1) Install SSL dependencies
#####
apk add --no-cache libressl
apk add --no-cache openssl


#####
# (2) Create SSL artifacts (see: https://dunne.io/vault-and-self-signed-ssl-certificates)
#####

# Clean up SSL workspace
cd /vault/config/ssl
rm -Rf *
cp ../libressl.conf .

# Create a CA root certificate and key
openssl req -newkey rsa:4096 \
    -days 3650 \
    -x509 \
    -nodes \
    -out root-cert.pem \
    -keyout root-privkey.pem \
    -subj '/C=US/ST=GA/L=Atlanta/O=BetterCloud/CN=localhost'

# Create a private key, and a certificate-signing request
openssl req -newkey rsa:4096 \
    -nodes \
    -out vault-csr.pem \
    -keyout vault-privkey.pem \
    -subj '/C=US/ST=GA/L=Atlanta/O=BetterCloud/CN=localhost'

# Create an X509 certificate for the Vault server
echo 000a > serialfile
touch certindex
openssl ca -batch -config libressl.conf -notext -in vault-csr.pem -out vault-cert.pem

# Configure SSL at the OS level to trust the new certs
cp root-cert.pem vault-cert.pem /usr/local/share/ca-certificates

# Clean up temp files
rm 0A.pem certindex certindex.attr certindex.old libressl.conf serialfile serialfile.old vault-csr.pem


#####
# (3) Start Vault
#####
vault server -config /vault/config/config.json
