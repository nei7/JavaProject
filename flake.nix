{
  description = "Java project";

  inputs = {
    nixpkgs.url = "github:nixos/nixpkgs/nixos-unstable";
  };

  outputs =
    { self, nixpkgs }:
    let
      system = "x86_64-linux";
      pkgs = nixpkgs.legacyPackages.${system};
    in
    {
      devShells.${system}.default = pkgs.mkShell {

        buildInputs = with pkgs; [

          jdk25_headless
          maven
          graphviz
        ];

        shellHook = ''
          export JAVA_HOME=${pkgs.jdk25_headless}
        '';
      };
    };
}
