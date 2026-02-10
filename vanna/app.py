from dotenv import load_dotenv

load_dotenv()

import os
from functools import wraps

from flask import Flask, Response, jsonify, request

from cache import MemoryCache


def build_vanna():
    provider = os.getenv("VANNA_PROVIDER", "ollama").lower()
    if provider == "ollama":
        from vanna.legacy.ollama import Ollama

        model = os.getenv("OLLAMA_MODEL", "qwen2.5:7b")
        host = os.getenv("OLLAMA_HOST", "http://ollama:11434")
        return Ollama({"model": model, "ollama_host": host})
    from vanna.remote import VannaDefault

    model = os.environ["VANNA_MODEL"]
    api_key = os.environ["VANNA_API_KEY"]
    return VannaDefault(model=model, api_key=api_key)


app = Flask(__name__, static_url_path="")
cache = MemoryCache()
vn = build_vanna()
vn.dialect = os.getenv("VANNA_DIALECT", "mysql")
vn.connect_to_mysql(
    host=os.getenv("MYSQL_HOST", "mysql"),
    dbname=os.getenv("MYSQL_DATABASE", "intelligent_customer"),
    user=os.getenv("MYSQL_USER", "root"),
    password=os.getenv("MYSQL_PASSWORD", "root"),
    port=int(os.getenv("MYSQL_PORT", "3306")),
)


def requires_cache(fields):
    def decorator(func):
        @wraps(func)
        def decorated(*args, **kwargs):
            cache_id = request.args.get("id")
            if cache_id is None:
                return jsonify({"type": "error", "error": "No id provided"})

            for field in fields:
                if cache.get(id=cache_id, field=field) is None:
                    return jsonify({"type": "error", "error": f"No {field} found"})

            field_values = {field: cache.get(id=cache_id, field=field) for field in fields}
            field_values["id"] = cache_id
            return func(*args, **field_values, **kwargs)

        return decorated

    return decorator


@app.route("/api/v1/sql", methods=["POST"])
def generate_sql_v1():
    payload = request.get_json(silent=True) or {}
    question = payload.get("question")
    schema = payload.get("schema")
    dialect = payload.get("dialect")
    if not question:
        return jsonify({"error": "question is required"}), 400

    if dialect:
        vn.dialect = dialect
    if schema:
        question = f"{question}\n\n数据库结构:\n{schema}"

    sql = vn.generate_sql(question=question)
    return jsonify({"sql": sql, "columns": [], "confidence": None})


@app.route("/api/v0/generate_questions", methods=["GET"])
def generate_questions():
    return jsonify(
        {
            "type": "question_list",
            "questions": vn.generate_questions(),
            "header": "Here are some questions you can ask:",
        }
    )


@app.route("/api/v0/generate_sql", methods=["GET"])
def generate_sql():
    question = request.args.get("question")
    if question is None:
        return jsonify({"type": "error", "error": "No question provided"})

    cache_id = cache.generate_id(question=question)
    sql = vn.generate_sql(question=question)

    cache.set(id=cache_id, field="question", value=question)
    cache.set(id=cache_id, field="sql", value=sql)

    return jsonify({"type": "sql", "id": cache_id, "text": sql})


@app.route("/api/v0/run_sql", methods=["GET"])
@requires_cache(["sql"])
def run_sql(cache_id: str, sql: str):
    try:
        df = vn.run_sql(sql=sql)
        cache.set(id=cache_id, field="df", value=df)
        return jsonify(
            {
                "type": "df",
                "id": cache_id,
                "df": df.head(10).to_json(orient="records"),
            }
        )
    except Exception as exc:
        return jsonify({"type": "error", "error": str(exc)})


@app.route("/api/v0/download_csv", methods=["GET"])
@requires_cache(["df"])
def download_csv(cache_id: str, df):
    csv = df.to_csv()
    return Response(
        csv,
        mimetype="text/csv",
        headers={"Content-disposition": f"attachment; filename={cache_id}.csv"},
    )


@app.route("/api/v0/generate_plotly_figure", methods=["GET"])
@requires_cache(["df", "question", "sql"])
def generate_plotly_figure(cache_id: str, df, question, sql):
    try:
        code = vn.generate_plotly_code(
            question=question,
            sql=sql,
            df_metadata=f"Running df.dtypes gives:\n {df.dtypes}",
        )
        fig = vn.get_plotly_figure(plotly_code=code, df=df, dark_mode=False)
        fig_json = fig.to_json()
        cache.set(id=cache_id, field="fig_json", value=fig_json)
        return jsonify({"type": "plotly_figure", "id": cache_id, "fig": fig_json})
    except Exception as exc:
        return jsonify({"type": "error", "error": str(exc)})


@app.route("/api/v0/get_training_data", methods=["GET"])
def get_training_data():
    df = vn.get_training_data()
    return jsonify(
        {
            "type": "df",
            "id": "training_data",
            "df": df.head(25).to_json(orient="records"),
        }
    )


@app.route("/api/v0/remove_training_data", methods=["POST"])
def remove_training_data():
    payload = request.get_json(silent=True) or {}
    training_id = payload.get("id")
    if training_id is None:
        return jsonify({"type": "error", "error": "No id provided"})

    if vn.remove_training_data(id=training_id):
        return jsonify({"success": True})
    return jsonify({"type": "error", "error": "Couldn't remove training data"})


@app.route("/api/v0/train", methods=["POST"])
def add_training_data():
    payload = request.get_json(silent=True) or {}
    question = payload.get("question")
    sql = payload.get("sql")
    ddl = payload.get("ddl")
    documentation = payload.get("documentation")
    try:
        training_id = vn.train(
            question=question, sql=sql, ddl=ddl, documentation=documentation
        )
        return jsonify({"id": training_id})
    except Exception as exc:
        return jsonify({"type": "error", "error": str(exc)})


@app.route("/api/v0/generate_followup_questions", methods=["GET"])
@requires_cache(["df", "question", "sql"])
def generate_followup_questions(cache_id: str, df, question, sql):
    followup_questions = vn.generate_followup_questions(
        question=question, sql=sql, df=df
    )
    cache.set(id=cache_id, field="followup_questions", value=followup_questions)
    return jsonify(
        {
            "type": "question_list",
            "id": cache_id,
            "questions": followup_questions,
            "header": "Here are some followup questions you can ask:",
        }
    )


@app.route("/api/v0/load_question", methods=["GET"])
@requires_cache(["question", "sql", "df", "fig_json", "followup_questions"])
def load_question(cache_id: str, question, sql, df, fig_json, followup_questions):
    return jsonify(
        {
            "type": "question_cache",
            "id": cache_id,
            "question": question,
            "sql": sql,
            "df": df.head(10).to_json(orient="records"),
            "fig": fig_json,
            "followup_questions": followup_questions,
        }
    )


@app.route("/", methods=["GET"])
def index():
    return jsonify({"status": "ok"})


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=int(os.getenv("PORT", "8081")))
