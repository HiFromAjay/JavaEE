GRANT ALL PRIVILEGES ON SCHEMA EPF_SECURITY TO Any_Role;
REVOKE INSERT ON SCHEMA EPF_SECURITY FROM Any_Role;
REVOKE UPDATE ON SCHEMA EPF_SECURITY FROM Any_Role;
REVOKE DELETE ON SCHEMA EPF_SECURITY FROM Any_Role;

GRANT ALL PRIVILEGES ON SCHEMA INFORMATION_SCHEMA TO Any_Role;
REVOKE INSERT ON SCHEMA INFORMATION_SCHEMA FROM Any_Role;
REVOKE UPDATE ON SCHEMA INFORMATION_SCHEMA FROM Any_Role;
REVOKE DELETE ON SCHEMA INFORMATION_SCHEMA FROM Any_Role;