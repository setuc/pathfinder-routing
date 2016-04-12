require 'sinatra/base'

class MasterRouter < Sinatra::Base
  post '/' do
    puts 'Route request received'
    request.body.rewind
    request_payload = JSON.parse request.body.read
  end
end
