# Shared Miniforge3 runtime. User-created environments and package caches stay
# under the user's persistent home directory rather than modifying /opt.
if [ -x /opt/miniforge3/bin/conda ]; then
    case ":$PATH:" in
        *:/opt/miniforge3/bin:*) ;;
        *) PATH="/opt/miniforge3/bin:/opt/miniforge3/condabin:$PATH" ;;
    esac
    export PATH

    export CONDA_ENVS_PATH="${CONDA_ENVS_PATH:-$HOME/.conda/envs}"
    export CONDA_PKGS_DIRS="${CONDA_PKGS_DIRS:-$HOME/.conda/pkgs}"

    if [ -n "${BASH_VERSION:-}" ] && [ -f /opt/miniforge3/etc/profile.d/conda.sh ]; then
        . /opt/miniforge3/etc/profile.d/conda.sh
    fi
fi
