INSERT INTO roles (id, code) VALUES (1, 'ADMIN'), (2, 'STAFF'), (3, 'CUSTOMER');

INSERT INTO users (id, email, password_hash, full_name, role_id, enabled) VALUES
  (1, 'admin@digishopkart.local', '$2b$10$Yyj3zq/a2GTN/G2oHhyhIe6quh3Jxy0oPfsBiXE0z1oyFx1fmzQ2S', 'Asha Admin', 1, 1),
  (2, 'staff@digishopkart.local', '$2b$10$ncw7prXEZjbobLU5mHykke/nJEP1L.nhy7J4rFlA9q9H19qIIDw7C', 'Rohan Staff', 2, 1),
  (3, 'customer@digishopkart.local', '$2b$10$/4.tVotIhUjI.448XzvXB.OoJDCQhAYp08XMFjoBHag4pJGVoiQzu', 'Meera Customer', 3, 1);
