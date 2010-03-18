# Be sure to restart your server when you modify this file.

# Your secret key for verifying cookie session data integrity.
# If you change this key, all old sessions will become invalid!
# Make sure the secret is at least 30 characters and all random, 
# no regular words or you'll be exposed to dictionary attacks.
ActionController::Base.session = {
  :key         => '_public_session',
  :secret      => '027387ba869c837f16342b02a4228bd2c8713fb1298f362d4d3f78996c14187c0e892aac576d6ba1ba8dd290fd034de0eb75de18a21164aba72a8a5c3eb9fd98'
}

# Use the database for sessions instead of the cookie-based default,
# which shouldn't be used to store highly confidential information
# (create the session table with "rake db:sessions:create")
# ActionController::Base.session_store = :active_record_store
