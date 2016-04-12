require 'httparty'
require 'json'
require 'sinatra/base'
require 'sinatra/config_file'
require 'thread'
require 'timeout'

class MasterRouter < Sinatra::Base
  register Sinatra::ConfigFile

  config_file 'config.yml'

  post '/' do
    puts 'Route request received'
    request.body.rewind
    request_payload = JSON.parse request.body.read

    threads = []
    settings.workers.each do |u|
      threads << Thread.new {
        begin
          Thread.current[:output] = HTTParty.post('http://routing2.thepathfinder.xyz', {
            :body => request_payload.to_json,
            :headers => { 'Content-Type' => 'application/json' },
            :timeout => settings.timeout
          }).parsed_response
        rescue TimeoutError
          Thread.current[:output] = nil
        end
      }
    end

    routes = []
    threads.each do |t|
      t.join
      routes << t[:output] unless t[:output].nil?
    end
    puts routes
    routes.first.to_json
  end
end
